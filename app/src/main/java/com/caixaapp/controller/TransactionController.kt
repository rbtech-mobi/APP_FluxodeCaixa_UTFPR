package com.caixaapp.controller

import android.content.Context
import com.caixaapp.model.MonthlySummary
import com.caixaapp.model.Person
import com.caixaapp.model.Transaction
import com.caixaapp.model.TransactionType
import com.caixaapp.repository.TransactionRepository
import com.caixaapp.util.DateUtils
import com.caixaapp.util.JsonUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionController(
    private val repository: TransactionRepository,
    private val context: Context
) {
    val people: List<Person> = JsonUtils.loadPeople(context)
    private val rateio: Map<String, Double> = JsonUtils.loadRateio(context)

    suspend fun add(transaction: Transaction) {
        repository.add(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        repository.delete(transaction)
    }

    suspend fun getStatement(personId: String): StatementResult {
        val items = buildAdjustedTransactions(personId, rateio)
        val saldo = calculateSaldo(items)
        return StatementResult(items, saldo)
    }

    suspend fun getMonthlySummary(personId: String): MonthlySummaryResult {
        val adjusted = buildAdjustedTransactions(personId, rateio)
        val now = LocalDate.now()
        val months = (-2..4).map { now.minusMonths(it.toLong()).withDayOfMonth(1) }.reversed()

        val summaries = months.map { month ->
            val monthLabel = DateUtils.formatChartDate(month)
            val monthTransactions = adjusted.filter { it.data.month == month.month && it.data.year == month.year }
            val totalCredito = monthTransactions.filter { it.tipo == TransactionType.CREDITO }.sumOf { it.valor }
            val totalDebito = monthTransactions.filter { it.tipo == TransactionType.DEBITO }.sumOf { it.valor }
            MonthlySummary(monthLabel, totalCredito, totalDebito)
        }
        val totalCredito = summaries.sumOf { it.totalCredito }
        val totalDebito = summaries.sumOf { it.totalDebito }
        return MonthlySummaryResult(summaries, totalCredito, totalDebito, totalCredito - totalDebito)
    }

    suspend fun getSummaryForCurrentMonth(personId: String, month: Int, year: Int): MonthlySummaryResult {
        val adjusted = buildAdjustedTransactions(personId, rateio)
        val monthDate = LocalDate.of(year, month, 1)

        val monthTransactions = adjusted.filter { it.data.monthValue == month && it.data.year == year }
        val totalCredito = monthTransactions.filter { it.tipo == TransactionType.CREDITO }.sumOf { it.valor }
        val totalDebito = monthTransactions.filter { it.tipo == TransactionType.DEBITO }.sumOf { it.valor }
        val monthLabel = DateUtils.formatMonth(monthDate)

        val summary = MonthlySummary(monthLabel, totalCredito, totalDebito)
        return MonthlySummaryResult(listOf(summary), totalCredito, totalDebito, totalCredito - totalDebito)
    }

    suspend fun getAllTransactions(): List<Transaction> {
        return repository.getAll()
    }

    private suspend fun buildStatementItems(personId: String): List<Transaction> {
        return buildAdjustedTransactions(personId, rateio).sortedByDescending { it.data }
    }

    private suspend fun buildAdjustedTransactions(personId: String, rateio: Map<String, Double>): List<Transaction> {
        val all = repository.getAll()
        
        if (personId == FAMILIA_ID) {
            return all 
        }

        val own = all.filter { it.pessoaId == personId }
        val rateioPercent = rateio[personId] ?: 0.0
        
        val familiaShared = all.filter { it.pessoaId == FAMILIA_ID }.map { transaction ->
            transaction.copy(
                id = 0,
                valor = transaction.valor * rateioPercent,
                descricao = "Rateio FAMILIA - ${transaction.descricao}",
                pessoaId = personId
            )
        }
        return own + familiaShared
    }

    private fun calculateSaldo(items: List<Transaction>): Double {
        val totalCredito = items.filter { it.tipo == TransactionType.CREDITO }.sumOf { it.valor }
        val totalDebito = items.filter { it.tipo == TransactionType.DEBITO }.sumOf { it.valor }
        return totalCredito - totalDebito
    }

    companion object {
        const val FAMILIA_ID = "00"
    }
}

data class StatementResult(
    val items: List<Transaction>,
    val saldo: Double
)

data class MonthlySummaryResult(
    val summaries: List<MonthlySummary>,
    val totalCredito: Double,
    val totalDebito: Double,
    val saldo: Double
)

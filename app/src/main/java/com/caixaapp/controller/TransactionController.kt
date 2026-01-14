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
        val items = buildAdjustedTransactions(personId)
        val saldo = calculateSaldo(items)
        return StatementResult(items, saldo)
    }

    suspend fun getMonthlySummary(personId: String): MonthlySummaryResult {
        val adjusted = buildAdjustedTransactions(personId)
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

    suspend fun getGlobalSummary(): GlobalSummaryResult {
        val allTransactions = repository.getAll()

        val totalCredito = allTransactions
            .filter { it.tipo == TransactionType.CREDITO }
            .sumOf { it.valor }

        val totalDebito = allTransactions
            .filter { it.tipo == TransactionType.DEBITO }
            .sumOf { it.valor }
            
        val saldo = totalCredito - totalDebito

        return GlobalSummaryResult(totalCredito, totalDebito, saldo)
    }

    suspend fun getAllTransactions(): List<Transaction> {
        return repository.getAll()
    }

    private suspend fun buildAdjustedTransactions(personId: String): List<Transaction> {
        val all = repository.getAll()

        if (personId == FAMILIA_ID) {
            return all.filter { it.pessoaId == FAMILIA_ID }.sortedByDescending { it.data }
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
        
        return (own + familiaShared).sortedByDescending { it.data }
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

data class GlobalSummaryResult(
    val totalCredito: Double,
    val totalDebito: Double,
    val saldo: Double
)

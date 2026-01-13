package com.caixaapp.repository

import com.caixaapp.model.Transaction
import com.caixaapp.model.TransactionType
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Concrete implementation of the TransactionRepository that uses Room.
 */
class RoomTransactionRepository(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun add(transaction: Transaction) {
        dao.insert(transaction.toEntity())
    }

    override suspend fun getAll(): List<Transaction> {
        return dao.getAll().map { it.toModel() }
    }

    override suspend fun delete(transaction: Transaction) {
        dao.deleteById(transaction.id)
    }

    override suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun getMonthlySum(type: String, startDate: Long, endDate: Long): Double {
        return dao.getSumByTypeInRange(type, startDate, endDate) ?: 0.0
    }
}


// --- Helper Functions to map between Model and Entity ---

private fun Transaction.toEntity(): TransactionEntity {
    // Converte LocalDate para Timestamp (Millis) para o banco
    val timestamp = this.data.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    
    return TransactionEntity(
        id = id,
        valor = valor,
        descricao = descricao,
        data = timestamp,
        tipo = tipo.name,
        pessoaId = pessoaId
    )
}

private fun TransactionEntity.toModel(): Transaction {
    // Converte Timestamp (Millis) do banco de volta para LocalDate
    val date = LocalDate.ofInstant(java.time.Instant.ofEpochMilli(data), ZoneOffset.UTC)
    
    return Transaction(
        id = id,
        valor = valor,
        descricao = descricao,
        data = date,
        tipo = TransactionType.valueOf(tipo),
        pessoaId = pessoaId
    )
}

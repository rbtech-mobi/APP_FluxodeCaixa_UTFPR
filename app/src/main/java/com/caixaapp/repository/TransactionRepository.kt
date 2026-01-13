package com.caixaapp.repository

import com.caixaapp.model.Transaction

interface TransactionRepository {
    suspend fun add(transaction: Transaction)
    suspend fun getAll(): List<Transaction>
    suspend fun delete(transaction: Transaction)
    suspend fun deleteById(id: Long)
    suspend fun getMonthlySum(type: String, startDate: Long, endDate: Long): Double
}

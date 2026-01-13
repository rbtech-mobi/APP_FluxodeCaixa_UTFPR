package com.caixaapp.repository

import com.caixaapp.model.Transaction

class FirebaseTransactionRepository(
    private val config: FirebaseConfig
) : TransactionRepository {

    override suspend fun add(transaction: Transaction) {
        if (!config.enabled) {
            return
        }
        // Implementação futura com Firebase Realtime Database.
    }

    override suspend fun getAll(): List<Transaction> {
        if (!config.enabled) {
            return emptyList()
        }
        // Implementação futura com Firebase Realtime Database.
        return emptyList()
    }

    override suspend fun delete(transaction: Transaction) {
        if (!config.enabled) {
            return
        }
        // Implementação futura com Firebase Realtime Database.
    }

    override suspend fun deleteById(id: Long) {
        if (!config.enabled) {
            return
        }
        // Implementação futura com Firebase Realtime Database.
    }

    override suspend fun getMonthlySum(type: String, startDate: Long, endDate: Long): Double {
        return 0.0
    }
}

data class FirebaseConfig(
    val databaseUrl: String,
    val enabled: Boolean
)

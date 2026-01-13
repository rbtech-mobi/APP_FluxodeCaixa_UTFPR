package com.caixaapp.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val valor: Double,
    val descricao: String,
    val data: Long, // Mudança para Long (Timestamp) para facilitar filtros de data
    val tipo: String,
    val pessoaId: String
)

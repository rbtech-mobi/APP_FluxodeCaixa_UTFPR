package com.caixaapp.model

import java.time.LocalDate

data class Transaction(
    val id: Long = 0,
    val valor: Double,
    val descricao: String,
    val data: LocalDate,
    val tipo: TransactionType,
    val pessoaId: String
)

enum class TransactionType(val label: String) {
    CREDITO("C"),
    DEBITO("D")
}

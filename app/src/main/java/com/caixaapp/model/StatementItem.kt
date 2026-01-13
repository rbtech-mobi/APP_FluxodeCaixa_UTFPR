package com.caixaapp.model

import java.time.LocalDate

data class StatementItem(
    val data: LocalDate,
    val tipo: TransactionType,
    val valor: Double,
    val descricao: String
)

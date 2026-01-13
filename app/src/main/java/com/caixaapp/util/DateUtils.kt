package com.caixaapp.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy")

    fun formatDate(date: LocalDate): String {
        return date.format(formatter)
    }

    fun formatMonth(date: LocalDate): String {
        return date.format(monthFormatter)
    }
}

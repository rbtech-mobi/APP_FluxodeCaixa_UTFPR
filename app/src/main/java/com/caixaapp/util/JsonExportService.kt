package com.caixaapp.util

import com.caixaapp.model.Transaction
import com.google.gson.GsonBuilder
import java.time.LocalDate

class JsonExportService {

    /**
     * Converts a list of Transaction objects into a formatted JSON string.
     * @param transactions The list of transactions to export.
     * @return A JSON formatted string.
     */
    fun export(transactions: List<Transaction>): String {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            // It's good practice to register a type adapter for custom objects or complex types like dates
            // to ensure they serialize correctly.
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .create()
        return gson.toJson(transactions)
    }
}
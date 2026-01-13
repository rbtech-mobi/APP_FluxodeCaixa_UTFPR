package com.caixaapp.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TransactionEntity::class],
    version = 2, // Incremented version to match schema changes
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}

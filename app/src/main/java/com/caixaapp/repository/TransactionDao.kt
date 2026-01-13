package com.caixaapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY data DESC")
    suspend fun getAll(): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT SUM(valor) FROM transactions WHERE tipo = :type AND data >= :startDate AND data <= :endDate")
    suspend fun getSumByTypeInRange(type: String, startDate: Long, endDate: Long): Double?
}
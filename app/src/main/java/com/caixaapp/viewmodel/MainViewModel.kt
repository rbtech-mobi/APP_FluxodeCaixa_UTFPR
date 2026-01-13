package com.caixaapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caixaapp.model.Transaction
import com.caixaapp.model.TransactionType
import com.caixaapp.repository.RoomTransactionRepository
import com.caixaapp.repository.TransactionRepository
import com.caixaapp.util.DatabaseProvider
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    
    private val _totalBalance = MutableLiveData<Double>(0.0)
    val totalBalance: LiveData<Double> = _totalBalance

    private val _monthlyIncome = MutableLiveData<Double>(0.0)
    val monthlyIncome: LiveData<Double> = _monthlyIncome

    private val _monthlyExpense = MutableLiveData<Double>(0.0)
    val monthlyExpense: LiveData<Double> = _monthlyExpense

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    init {
        val dao = DatabaseProvider.getDatabase(application).transactionDao()
        repository = RoomTransactionRepository(dao)
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            // 1. Saldo Total (Histórico completo - calculado em memória ou via Query)
            val all = repository.getAll()
            _transactions.value = all
            val total = all.sumOf { if (it.tipo == TransactionType.CREDITO) it.valor else -it.valor }
            _totalBalance.postValue(total)

            // 2. Cálculo robusto de Mês Atual usando Timestamps
            val now = LocalDate.now()
            val startOfMonth = now.withDayOfMonth(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()

            Log.d("DEBUG_SALDO", "Buscando receitas entre $startOfMonth e $endOfMonth (Mês: ${now.month})")

            // Busca somas diretamente do banco via Query otimizada
            val income = repository.getMonthlySum(TransactionType.CREDITO.name, startOfMonth, endOfMonth)
            val expense = repository.getMonthlySum(TransactionType.DEBITO.name, startOfMonth, endOfMonth)
            
            _monthlyIncome.postValue(income)
            _monthlyExpense.postValue(expense)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
            loadData()
        }
    }
    
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.add(transaction)
            loadData()
        }
    }
}

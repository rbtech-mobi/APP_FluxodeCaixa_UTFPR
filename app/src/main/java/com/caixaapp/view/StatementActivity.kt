package com.caixaapp.view

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.caixaapp.adapter.StatementAdapter
import com.caixaapp.controller.TransactionController
import com.caixaapp.databinding.ActivityStatementBinding
import com.caixaapp.model.Person
import com.caixaapp.repository.RoomTransactionRepository
import com.caixaapp.util.DatabaseProvider
import com.caixaapp.util.JsonUtils
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

class StatementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatementBinding
    private lateinit var people: List<Person>
    private lateinit var adapter: StatementAdapter
    private lateinit var controller: TransactionController
    private val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityStatementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = DatabaseProvider.getDatabase(this).transactionDao()
        controller = TransactionController(RoomTransactionRepository(dao))

        people = JsonUtils.loadPeople(this)
        setupRecycler()
        setupSpinner()

        binding.backToMenuButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecycler() {
        adapter = StatementAdapter(emptyList()) { transaction ->
            lifecycleScope.launch {
                controller.deleteTransaction(transaction)
                loadStatementForSelectedPerson()
            }
        }
        binding.statementRecycler.layoutManager = LinearLayoutManager(this)
        binding.statementRecycler.adapter = adapter
    }

    private fun setupSpinner() {
        val personNames = (listOf(Person("00", "FAMILIA")) + people).map { it.nome }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, personNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterSpinner.adapter = spinnerAdapter

        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                loadStatementForSelectedPerson()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadStatementForSelectedPerson() {
        val selectedPersonName = binding.filterSpinner.selectedItem as String
        val selectedPerson = (listOf(Person("00", "FAMILIA")) + people).find { it.nome == selectedPersonName }
        val personId = selectedPerson?.id ?: TransactionController.FAMILIA_ID

        val rateio = JsonUtils.loadRateio(this)

        lifecycleScope.launch {
            val result = controller.getStatement(personId, rateio)
            runOnUiThread {
                adapter.update(result.items.sortedByDescending { it.data })
                binding.statementSummary.text = "Saldo total: ${formatter.format(result.saldo)}"
            }
        }
    }
}

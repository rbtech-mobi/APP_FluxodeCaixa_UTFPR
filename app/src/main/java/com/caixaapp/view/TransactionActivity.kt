package com.caixaapp.view

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.caixaapp.databinding.ActivityTransactionBinding
import com.caixaapp.model.Person
import com.caixaapp.model.Transaction
import com.caixaapp.model.TransactionType
import com.caixaapp.util.CurrencyMaskWatcher
import com.caixaapp.util.DateUtils
import com.caixaapp.util.JsonUtils
import com.caixaapp.viewmodel.MainViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class TransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionBinding
    private lateinit var people: List<Person>
    private lateinit var viewModel: MainViewModel
    private var selectedDate: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.backToMenuButton.setOnClickListener { finish() }

        people = JsonUtils.loadPeople(this)
        setupPessoaSpinner()
        setupDatePicker()
        setupTypeSelector()
        
        binding.valueInput.addTextChangedListener(CurrencyMaskWatcher(binding.valueInput))
        binding.saveButton.setOnClickListener { saveTransaction() }
    }

    private fun setupPessoaSpinner() {
        val labels = people.map { it.nome }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        binding.pessoaSpinner.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.dateInput.setText(DateUtils.formatDate(selectedDate))
        binding.dateInput.setOnClickListener { showMaterialDatePicker() }
    }

    private fun showMaterialDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a Data")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate()
            selectedDate = date
            binding.dateInput.setText(DateUtils.formatDate(selectedDate))
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun setupTypeSelector() {
        binding.typeCredit.isChecked = true
    }

    private fun saveTransaction() {
        if (people.isEmpty() || binding.pessoaSpinner.selectedItemPosition < 0) {
            Toast.makeText(this, "Erro ao carregar os dados de pessoas.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedIndex = binding.pessoaSpinner.selectedItemPosition
        val personId = people[selectedIndex].id
        
        val type = if (binding.typeToggleGroup.checkedButtonId == com.caixaapp.R.id.typeCredit) 
            TransactionType.CREDITO else TransactionType.DEBITO
        
        val rawValue = binding.valueInput.text?.toString().orEmpty()
            .replace("[R$\u00A0,\\.]".toRegex(), "")
        
        val value = rawValue.toDoubleOrNull()?.div(100)
        val description = binding.descriptionInput.text?.toString().orEmpty()

        if (value == null || value <= 0) {
            Toast.makeText(this, "Informe um valor válido", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (description.isBlank()) {
            Toast.makeText(this, "Preencha a descrição", Toast.LENGTH_SHORT).show()
            return
        }

        val transaction = Transaction(
            valor = value,
            descricao = description,
            data = selectedDate,
            tipo = type,
            pessoaId = personId
        )

        viewModel.addTransaction(transaction)
        Toast.makeText(this, "Lançamento salvo", Toast.LENGTH_SHORT).show()
        finish() 
    }
}

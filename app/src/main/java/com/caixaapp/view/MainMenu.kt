package com.caixaapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.caixaapp.R
import com.caixaapp.controller.TransactionController
import com.caixaapp.databinding.MainMenuBinding
import com.caixaapp.model.Person
import com.caixaapp.repository.RoomTransactionRepository
import com.caixaapp.util.DatabaseProvider
import com.caixaapp.util.ExportService
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

class MainMenu : AppCompatActivity() {

    private lateinit var binding: MainMenuBinding
    private lateinit var controller: TransactionController
    private lateinit var exportService: ExportService
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = DatabaseProvider.getDatabase(this).transactionDao()
        controller = TransactionController(RoomTransactionRepository(dao), this)
        exportService = ExportService()

        setupSpinner()
        setupButtonClickListeners()
    }

    override fun onResume() {
        super.onResume()
        updateDashboardData()
    }

    private fun getPeopleForSpinner(): List<Person> {
        return controller.people
    }

    private fun setupSpinner() {
        val personDescriptions = getPeopleForSpinner().map { it.descricao }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, personDescriptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterSpinner.adapter = adapter

        binding.filterSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                updateDashboardData()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun updateDashboardData() {
        val selectedPersonDescription = binding.filterSpinner.selectedItem as String
        val selectedPerson = getPeopleForSpinner().find { it.descricao == selectedPersonDescription }
        val personId = selectedPerson?.id ?: TransactionController.FAMILIA_ID
        
        lifecycleScope.launch {
            try {
                val result = controller.getStatement(personId)

                runOnUiThread {
                    binding.textTotalBalance.text = currencyFormatter.format(result.saldo)
                    binding.textIncomeValue.text = currencyFormatter.format(result.items.filter { it.tipo == com.caixaapp.model.TransactionType.CREDITO }.sumOf { it.valor })
                    binding.textExpenseValue.text = currencyFormatter.format(result.items.filter { it.tipo == com.caixaapp.model.TransactionType.DEBITO }.sumOf { it.valor })
                }
            } catch (e: Exception) {
                Log.e("DASHBOARD_ERROR", getString(R.string.error_general), e)
            }
        }
    }

    private fun setupButtonClickListeners() {
        binding.buttonGoToTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        binding.buttonGoToStatement.setOnClickListener {
            startActivity(Intent(this, StatementActivity::class.java))
        }

        binding.buttonGoToChart.setOnClickListener {
            startActivity(Intent(this, ChartActivity::class.java))
        }

        binding.buttonExport.setOnClickListener {
            showExportOptionsDialog()
        }

        binding.buttonSync.setOnClickListener {
            showFutureFeatureDialog()
        }

        binding.buttonExit.setOnClickListener {
            finishAffinity()
        }
    }

    private fun showExportOptionsDialog() {
        val options = arrayOf(getString(R.string.export_format_pdf), getString(R.string.export_format_xml), getString(R.string.export_format_json))
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_export_title))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> handleExport("pdf")
                    1 -> handleExport("xml")
                    2 -> handleExport("json")
                }
            }
            .show()
    }

    private fun handleExport(format: String) {
        lifecycleScope.launch {
            val transactions = controller.getAllTransactions()
            if (transactions.isEmpty()) {
                Toast.makeText(this@MainMenu, getString(R.string.msg_export_empty), Toast.LENGTH_SHORT).show()
                return@launch
            }

            val fileName = "export_caixa_${System.currentTimeMillis()}.$format"
            val file = File(cacheDir, fileName)
            
            try {
                when (format) {
                    "json" -> file.writeText(exportService.toEmptyJson(transactions))
                    "xml" -> file.writeText(exportService.toXml(transactions))
                    "pdf" -> {
                        val pdf = exportService.createPdf(this@MainMenu, transactions)
                        FileOutputStream(file).use { out ->
                            pdf.writeTo(out)
                        }
                        pdf.close()
                    }
                }
                shareFile(file, format)
            } catch (e: Exception) {
                Toast.makeText(this@MainMenu, getString(R.string.msg_export_error, e.message), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun shareFile(file: File, format: String) {
        val uri: Uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        val mimeType = when (format) {
            "pdf" -> "application/pdf"
            "xml" -> "text/xml"
            else -> "application/json"
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_title, format)))
    }

    private fun showFutureFeatureDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_future_feature_title))
            .setMessage(getString(R.string.dialog_future_feature_message))
            .setPositiveButton(getString(R.string.dialog_button_ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}

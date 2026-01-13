package com.caixaapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.caixaapp.controller.TransactionController
import com.caixaapp.databinding.MainMenuBinding
import com.caixaapp.repository.RoomTransactionRepository
import com.caixaapp.util.DatabaseProvider
import com.caixaapp.util.ExportService
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
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

        setupButtonClickListeners()
    }

    override fun onResume() {
        super.onResume()
        updateDashboardData()
    }

    private fun updateDashboardData() {
        lifecycleScope.launch {
            val personId = TransactionController.FAMILIA_ID 
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentYear = calendar.get(Calendar.YEAR)

            try {
                val result = controller.getSummaryForCurrentMonth(personId, currentMonth, currentYear)

                runOnUiThread {
                    val monthName = SimpleDateFormat("MMMM", Locale("pt", "BR")).format(calendar.time)
                    binding.textCurrentMonth.text = "Movimentação do mês de ${monthName.replaceFirstChar { it.uppercase() }}"

                    val totalBalance = result.saldo
                    binding.textTotalBalance.text = currencyFormatter.format(totalBalance)
                    
                    if (totalBalance == 0.0) {
                        Log.d("DEBUG_SALDO", "Total calculado: 0. Visão FAMÍLIA (Agregada)")
                    }
                    
                    binding.textIncomeValue.text = currencyFormatter.format(result.totalCredito)
                    binding.textExpenseValue.text = currencyFormatter.format(result.totalDebito)
                }
            } catch (e: Exception) {
                Log.e("DASHBOARD_ERROR", "Erro ao carregar dados", e)
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
        val options = arrayOf("PDF", "XML", "JSON")
        AlertDialog.Builder(this)
            .setTitle("Escolha o formato de exportação")
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
                Toast.makeText(this@MainMenu, "Não há lançamentos para exportar", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainMenu, "Erro ao exportar: ${e.message}", Toast.LENGTH_LONG).show()
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
        startActivity(Intent.createChooser(intent, "Compartilhar $format"))
    }

    private fun showFutureFeatureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Funcionalidade Futura")
            .setMessage("Sincronização com API (pós-aula API).")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}

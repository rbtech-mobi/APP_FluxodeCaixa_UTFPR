package com.caixaapp.view

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caixaapp.R
import com.caixaapp.controller.TransactionController
import com.caixaapp.databinding.ActivityChartBinding
import com.caixaapp.model.Person
import com.caixaapp.repository.RoomTransactionRepository
import com.caixaapp.util.DatabaseProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch

class ChartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChartBinding
    private lateinit var controller: TransactionController

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = DatabaseProvider.getDatabase(this).transactionDao()
        controller = TransactionController(RoomTransactionRepository(dao), this)

        setupSpinner()
        setupChart()

        binding.backToMenuButton.setOnClickListener { finish() }
    }

    private fun getPeopleForSpinner(): List<Person> {
        return controller.people
    }

    private fun setupSpinner() {
        val personDescriptions = getPeopleForSpinner().map { it.descricao }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, personDescriptions)
        binding.chartFilterSpinner.adapter = adapter
        binding.chartFilterSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                loadChartData()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
    }

    private fun setupChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setFitBars(true)
            legend.isEnabled = true
            
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(true)
            xAxis.granularity = 1f
            
            axisLeft.setDrawGridLines(true)
            axisLeft.setDrawZeroLine(true)
            axisRight.isEnabled = false
        }
    }

    private fun loadChartData() {
        val selectedPersonDescription = binding.chartFilterSpinner.selectedItem as String
        val selectedPerson = getPeopleForSpinner().find { it.descricao == selectedPersonDescription }
        val personId = selectedPerson?.id ?: TransactionController.FAMILIA_ID

        lifecycleScope.launch {
            val summaryResult = controller.getMonthlySummary(personId)
            val summaries = summaryResult.summaries

            val creditEntries = ArrayList<BarEntry>()
            val debitEntries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()

            summaries.forEachIndexed { index, summary ->
                creditEntries.add(BarEntry(index.toFloat(), summary.totalCredito.toFloat()))
                debitEntries.add(BarEntry(index.toFloat(), -summary.totalDebito.toFloat()))
                labels.add(summary.monthLabel)
            }

            val creditDataSet = BarDataSet(creditEntries, getString(R.string.label_credit)).apply {
                color = Color.GREEN
            }
            val debitDataSet = BarDataSet(debitEntries, getString(R.string.label_debit)).apply {
                color = Color.RED
            }

            val barData = BarData(creditDataSet, debitDataSet)
            
            val groupSpace = 0.3f
            val barSpace = 0.05f
            val barWidth = 0.3f
            barData.barWidth = barWidth

            runOnUiThread {
                binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                binding.barChart.xAxis.labelCount = labels.size
                binding.barChart.data = barData
                binding.barChart.groupBars(0f, groupSpace, barSpace)
                binding.barChart.invalidate()
            }
        }
    }
}

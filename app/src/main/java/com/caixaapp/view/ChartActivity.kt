package com.caixaapp.view

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
import com.caixaapp.util.JsonUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

class ChartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChartBinding
    private lateinit var people: List<Person>
    private lateinit var controller: TransactionController
    private val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = DatabaseProvider.getDatabase(this).transactionDao()
        controller = TransactionController(RoomTransactionRepository(dao))

        people = JsonUtils.loadPeople(this)
        setupSpinner()
        setupChart()

        binding.backToMenuButton.setOnClickListener {
            finish()
        }
    }

    private fun setupSpinner() {
        val labels = people.map { it.nome }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        binding.chartFilterSpinner.adapter = adapter
        binding.chartFilterSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                loadChart()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
    }

    private fun setupChart() {
        binding.lineChart.description.isEnabled = false
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.lineChart.xAxis.granularity = 1f
        binding.lineChart.setTouchEnabled(true)
        binding.lineChart.setPinchZoom(true)
    }

    private fun loadChart() {
        if (people.isEmpty() || binding.chartFilterSpinner.selectedItemPosition < 0) {
            return
        }

        val rateio = JsonUtils.loadRateio(this)
        val personId = people[binding.chartFilterSpinner.selectedItemPosition].id

        lifecycleScope.launch {
            val result = controller.getMonthlySummary(personId, rateio)
            val labels = result.summaries.map { it.monthLabel }

            // Calculate accumulated balance for the Line Chart
            var accumulated = 0.0
            val balanceEntries = result.summaries.mapIndexed { index, summary ->
                accumulated += (summary.totalCredito - summary.totalDebito)
                Entry(index.toFloat(), accumulated.toFloat())
            }

            val dataSet = LineDataSet(balanceEntries, "Tendência de Saldo")
            dataSet.color = getColor(R.color.primary)
            dataSet.setCircleColor(getColor(R.color.primary))
            dataSet.lineWidth = 3f
            dataSet.circleRadius = 5f
            dataSet.setDrawValues(false)
            dataSet.setDrawFilled(true)
            dataSet.fillColor = getColor(R.color.primary)
            dataSet.fillAlpha = 30
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

            val lineData = LineData(dataSet)

            runOnUiThread {
                binding.textCurrentBalance.text = formatter.format(result.saldo)
                // Simulated projection for demo purposes (last month trend + 10%)
                binding.textProjectedBalance.text = formatter.format(result.saldo * 1.05)

                binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                binding.lineChart.data = lineData
                binding.lineChart.invalidate()
            }
        }
    }
}

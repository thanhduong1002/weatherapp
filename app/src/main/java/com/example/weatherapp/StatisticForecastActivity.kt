package com.example.weatherapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.ActivityStatisticForecastBinding
import com.example.weatherapp.valueformat.CelsiusValueFormatter
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatisticForecastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticForecastBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private var description: Description = Description()
    private var xValues: MutableList<String> = mutableListOf()
    private lateinit var xAxis: XAxis
    private lateinit var yAxis: YAxis
    private lateinit var yRightAxis: YAxis
    private var entries1: MutableList<Entry> = mutableListOf()
    private lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatisticForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        observeForecastLiveData()

        weatherViewModel.callForecastAPI(16.083, 108.0, getString(R.string.apikey))
    }

    private fun showLineChart() {
        description.text = "Temperature Trend over Time"
        description.textColor = Color.RED
        description.textSize = 16f
        description.setPosition(800f, 30f)
        binding.lineChart.description = description

        xAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        xAxis.granularity = 1f
        xAxis.labelCount = 32

        yAxis = binding.lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 50f
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.BLACK
        yAxis.labelCount = 10
        yAxis.valueFormatter = CelsiusValueFormatter()

        yRightAxis = binding.lineChart.axisRight
        yRightAxis.setDrawLabels(false)

        lineDataSet = LineDataSet(entries1, "Temp")
        lineDataSet.color = Color.RED
        lineDataSet.lineWidth = 3f

        lineData = LineData(lineDataSet)

        binding.lineChart.data = lineData

        binding.lineChart.invalidate()
    }

    private fun observeForecastLiveData() {
        weatherViewModel.forecastLiveData.observe(this) { forecast ->
            for ((index, item) in forecast.withIndex()) {
                item.dt?.let { convertUnixTimestampToDateTime(it.toLong()) }
                    ?.let { xValues.add(it) }

                val tempCelsius = item.main?.temp?.let { kelvinToCelsius(it) }
                if (tempCelsius != null) {
                    entries1.add(Entry(index.toFloat(), tempCelsius))
                }
            }
            showLineChart()
        }
    }

    private fun convertUnixTimestampToDateTime(unixTimestamp: Long): String {
        val dateFormat = SimpleDateFormat("HH'h' dd/MM", Locale.getDefault())
        val date = Date(unixTimestamp * 1000)

        return dateFormat.format(date)
    }

    private fun kelvinToCelsius(kelvin: Double): Float {
        val celsius = kelvin - 273.15
        val decimalFormat = DecimalFormat("#.##")

        return decimalFormat.format(celsius).toFloat()
    }
}
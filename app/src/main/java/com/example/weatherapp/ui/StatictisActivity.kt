package com.example.weatherapp.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityStatictisBinding
import com.example.weatherapp.valueformat.CelsiusValueFormatter
import com.example.weatherapp.valueformat.MyValueFormatter
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatictisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatictisBinding
    private var description: Description = Description()
    private var xValues: MutableList<String> = mutableListOf()
    private lateinit var xAxis: XAxis
    private lateinit var yAxis: YAxis
    private lateinit var yRightAxis: YAxis
    private var entries1: MutableList<Entry> = mutableListOf()
    private var barEntries1: MutableList<BarEntry> = mutableListOf()
    private lateinit var lineDataSet: LineDataSet
    private lateinit var barDataSet: BarDataSet
    private lateinit var weatherViewModel: WeatherViewModel
    private var combinedData: CombinedData = CombinedData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatictisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        weatherViewModel.forecastLiveData.observe(this) { forecast ->
            for ((index, item) in forecast.withIndex()) {
                item.dt?.let { convertUnixTimestampToDateTime(it.toLong()) }
                    ?.let { xValues.add(it) }

                val tempCelsius = item.main?.temp?.let { kelvinToCelsius(it) }
                if (tempCelsius != null) {
                    entries1.add(Entry(index.toFloat(), tempCelsius))
                }

                val humidity = item.main?.humidity
                if (humidity != null) {
                    barEntries1.add(BarEntry(index.toFloat(), humidity.toFloat()))
                }
            }
            showCombinedChart()
        }

        weatherViewModel.callForecastAPI(16.083, 108.0, getString(R.string.apikey))
    }

    private fun showCombinedChart() {
        description.apply {
            text = "Temp and Humidity Record"
            textColor = Color.RED
            textSize = 16f
            setPosition(800f, 30f)
        }
        binding.chartCombined.description = description

        xAxis = binding.chartCombined.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(xValues)
            granularity = 1f
            labelCount = 32
        }

        yAxis = binding.chartCombined.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = 100f
            axisLineWidth = 2f
            axisLineColor = Color.BLACK
            labelCount = 10
            valueFormatter = CelsiusValueFormatter()
        }

        yRightAxis = binding.chartCombined.axisRight.apply {
            axisMinimum = 0f
            axisMaximum = 100f
            axisLineWidth = 2f
            axisLineColor = Color.BLACK
            labelCount = 10
            valueFormatter = MyValueFormatter()
        }

        barDataSet = BarDataSet(barEntries1, "Humidity").apply {
            color = Color.BLUE
        }
        lineDataSet = LineDataSet(entries1, "Temp").apply {
            color = Color.RED
            lineWidth = 3f
        }

        combinedData.apply {
            setData(BarData(barDataSet))
            setData(LineData(lineDataSet))
        }

        binding.chartCombined.data = combinedData

        binding.chartCombined.invalidate()
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
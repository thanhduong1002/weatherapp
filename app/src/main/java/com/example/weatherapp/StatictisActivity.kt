package com.example.weatherapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var lineData: LineData
    private lateinit var barData: BarData
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
        description.text = "Temp and Humidity Record"
        description.textColor = Color.RED
        description.textSize = 16f
        description.setPosition(800f, 30f)
        binding.chartCombined.description = description

        xAxis = binding.chartCombined.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        xAxis.granularity = 1f
        xAxis.labelCount = 32

        yAxis = binding.chartCombined.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 100f
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.BLACK
        yAxis.labelCount = 10
        yAxis.valueFormatter = CelsiusValueFormatter()

        yRightAxis = binding.chartCombined.axisRight
        yRightAxis.axisMinimum = 0f
        yRightAxis.axisMaximum = 100f
        yRightAxis.axisLineWidth = 2f
        yRightAxis.axisLineColor = Color.BLACK
        yRightAxis.labelCount = 10
        yRightAxis.valueFormatter = MyValueFormatter()

        barDataSet = BarDataSet(barEntries1, "Humidity")
        barDataSet.color = Color.BLUE
        lineDataSet = LineDataSet(entries1, "Temp")
        lineDataSet.color = Color.RED
        lineDataSet.lineWidth = 3f

        barData = BarData(barDataSet)
        lineData = LineData(lineDataSet)

        combinedData.setData(barData)
        combinedData.setData(lineData)

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
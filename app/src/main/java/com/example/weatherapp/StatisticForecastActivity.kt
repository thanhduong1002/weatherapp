package com.example.weatherapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.ActivityStatisticForecastBinding
import com.example.weatherapp.valueformat.CelsiusValueFormatter
import com.example.weatherapp.valueformat.MyValueFormatter
import com.example.weatherapp.valueformat.PressureValueFormatter
import com.example.weatherapp.valueformat.SpeedValueFormatter
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

class StatisticForecastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticForecastBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private var description: Description = Description()
    private var xValues: MutableList<String> = mutableListOf()
    private lateinit var xAxis: XAxis
    private lateinit var yAxis: YAxis
    private lateinit var yRightAxis: YAxis
    private var barEntries: MutableList<BarEntry> = mutableListOf()
    private var entries: MutableList<Entry> = mutableListOf()
    private val emptyEntries: List<Entry> = emptyList()
    private val emptyBarEntries: List<BarEntry> = emptyList()
    private lateinit var lineDataSet: LineDataSet
    private lateinit var barDataSet: BarDataSet
    private lateinit var lineData: LineData
    private lateinit var barData: BarData
    private var combinedData: CombinedData = CombinedData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatisticForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        observeForecastLiveData()

        weatherViewModel.callForecastAPI(16.083, 108.0, getString(R.string.apikey))
    }

    private fun showLineChart() {
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
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.BLACK
        yAxis.labelCount = 10

        yRightAxis = binding.lineChart.axisRight
        yRightAxis.setDrawLabels(false)
    }

    private fun observeForecastLiveData() {
        weatherViewModel.forecastLiveData.observe(this) { forecast ->
            for ((index, item) in forecast.withIndex()) {
                item.dt?.let { convertUnixTimestampToDateTime(it.toLong()) }
                    ?.let { xValues.add(it) }

                val tempCelsius = item.main?.temp?.let { kelvinToCelsius(it) }
                if (tempCelsius != null) {
                    entries.add(Entry(index.toFloat(), tempCelsius))
                }
            }
            setupChartSpinner()
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

    private fun setupChartSpinner() {
        val topics = arrayOf("Temperature", "Humidity", "Atmospheric pressure", "Cloudiness", "Wind speed")

        val adapter = ArrayAdapter(this, R.layout.text_dropdown, topics)
        adapter.setDropDownViewResource(R.layout.spinner_text_dropdown)
        binding.spinnerChart.adapter = adapter

        binding.spinnerChart.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    setTopic(topics[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun setTopic(topic: String) {
        when (topic) {
            "Temperature" -> setEntryValues("Temperature")
            "Humidity" -> setEntryValues("Humidity")
            "Atmospheric pressure" -> setEntryValues("Atmospheric pressure")
            "Cloudiness" -> setEntryValues("Cloudiness")
            "Wind speed" -> setEntryValues("Wind speed")
        }
    }

    private fun setEntryValues(topic: String) {
        if (topic == "Temperature") {
            setDescriptionText("Temperature Trend over Time")
            setDataChart("line", topic)
        }
        else if (topic == "Atmospheric pressure") {
            setDescriptionText("Atmospheric pressure Trend over Time")

            for ((index, item) in weatherViewModel.forecastLiveData.value!!.withIndex()) {
                val pressure = item.main?.pressure
                if (pressure != null) {
                    entries.add(Entry(index.toFloat(), pressure.toFloat()))
                }
            }
            setDataChart("line", topic)
        }
        else if (topic == "Cloudiness") {
            setDescriptionText("Time-Based Cloudiness Chart")

            for ((index, item) in weatherViewModel.forecastLiveData.value!!.withIndex()) {
                val cloudiness = item.clouds?.all
                if (cloudiness != null) {
                    barEntries.add(BarEntry(index.toFloat(), cloudiness.toFloat()))
                }
            }
            setDataChart("bar", topic)
        }
        else if (topic == "Wind speed") {
            setDescriptionText("Wind speed pressure Trend over Time")

            for ((index, item) in weatherViewModel.forecastLiveData.value!!.withIndex()) {
                val speed = item.wind?.speed
                if (speed != null) {
                    entries.add(Entry(index.toFloat(), speed.toFloat()))
                }
            }
            setDataChart("line", topic)
        }
        else {
            setDescriptionText("Time-Based Humidity Chart")

            for ((index, item) in weatherViewModel.forecastLiveData.value!!.withIndex()) {
                val humidity = item.main?.humidity
                if (humidity != null) {
                    barEntries.add(BarEntry(index.toFloat(), humidity.toFloat()))
                }
            }
            setDataChart("bar", topic)
        }
    }

    private fun setDescriptionText(title: String) {
        description.text = title
    }

    private fun setDataChart(typeChart: String, topic: String) {
        showLineChart()
        lineDataSet = LineDataSet(emptyEntries, "")
        barDataSet = BarDataSet(emptyBarEntries, "")
        lineData = LineData(lineDataSet)
        barData = BarData(barDataSet)
        if (typeChart == "line") {
            if (topic == "Temperature") {
                yAxis.axisMinimum = 0f
                yAxis.axisMaximum = 50f
                yAxis.valueFormatter = CelsiusValueFormatter()

                lineDataSet = LineDataSet(entries, "Temp")
            }
            else if (topic == "Atmospheric pressure") {
                yAxis.axisMinimum = 970f
                yAxis.axisMaximum = 1030f
                yAxis.valueFormatter = PressureValueFormatter()

                lineDataSet = LineDataSet(entries, "Atmospheric pressure")
            }
            else {
                yAxis.axisMinimum = 0f
                yAxis.axisMaximum = 20f
                yAxis.valueFormatter = SpeedValueFormatter()

                lineDataSet = LineDataSet(entries, "Wind speed")
            }
            lineDataSet.color = Color.RED
            lineDataSet.lineWidth = 3f

            lineData = LineData(lineDataSet)
            combinedData.setData(lineData)
            combinedData.setData(barData)
        } else {
            yAxis.valueFormatter = MyValueFormatter()
            yAxis.axisMinimum = 0f
            yAxis.axisMaximum = 100f
            yAxis.axisLineWidth = 2f

            Log.d("topic", "setDataChart: $topic")
            barDataSet = if (topic == "Humidity") {
                BarDataSet(barEntries, "Humidity")
            } else {
                BarDataSet(barEntries, "Cloudiness")
            }
            barDataSet.color = Color.BLUE

            barData = BarData(barDataSet)
            combinedData.setData(barData)
            combinedData.setData(lineData)
        }
        binding.lineChart.data = combinedData

        binding.lineChart.invalidate()
    }
}
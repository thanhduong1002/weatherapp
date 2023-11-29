package com.example.weatherapp.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityStatisticForecastBinding
import com.example.weatherapp.model.ChartTopic
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
import com.github.mikephil.charting.formatter.ValueFormatter
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

    private fun showChart() {
        description.apply {
            textColor = Color.RED
            textSize = 16f
            setPosition(800f, 30f)
        }
        binding.mainChart.description = description

        xAxis = binding.mainChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(xValues)
            granularity = 1f
            labelCount = 32
        }

        yAxis = binding.mainChart.axisLeft.apply {
            axisLineWidth = 2f
            axisLineColor = Color.BLACK
            labelCount = 10
        }

        yRightAxis = binding.mainChart.axisRight.apply {
            setDrawLabels(false)
        }
    }

    private fun observeForecastLiveData() {
        weatherViewModel.forecastLiveData.observe(this) { forecast ->
            for (item in forecast) {
                item.dt?.let { convertUnixTimestampToDateTime(it.toLong()) }
                    ?.let { xValues.add(it) }
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
        val topics =
            arrayOf("Temperature", "Humidity", "Atmospheric pressure", "Cloudiness", "Wind speed")

        val adapter = ArrayAdapter(this, R.layout.text_dropdown, topics).apply {
            setDropDownViewResource(R.layout.spinner_text_dropdown)
        }
        binding.spinnerChart.apply {
            setAdapter(adapter)
            onItemSelectedListener =
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
        val chartTopic = when (topic) {
            "Temperature" -> ChartTopic(
                "Temperature Trend over Time",
                mutableListOf(),
                null,
                "line",
                topic
            )

            "Humidity" -> ChartTopic(
                "Time-Based Humidity Chart",
                mutableListOf(),
                mutableListOf(),
                "bar",
                topic
            )

            "Atmospheric pressure" -> ChartTopic(
                "Atmospheric pressure Trend over Time",
                mutableListOf(),
                null,
                "line",
                topic
            )

            "Cloudiness" -> ChartTopic(
                "Time-Based Cloudiness Chart",
                mutableListOf(),
                mutableListOf(),
                "bar",
                topic
            )

            "Wind speed" -> ChartTopic(
                "Wind speed pressure Trend over Time",
                mutableListOf(),
                null,
                "line",
                topic
            )

            else -> throw IllegalArgumentException("Unsupported topic: $topic")
        }

        for ((index, item) in weatherViewModel.forecastLiveData.value!!.withIndex()) {
            when (topic) {
                "Temperature" -> chartTopic.entryList.add(
                    Entry(index.toFloat(), kelvinToCelsius(item.main?.temp ?: 0.0))
                )

                "Humidity" -> chartTopic.barEntryList!!.add(
                    BarEntry(index.toFloat(), item.main?.humidity?.toFloat() ?: 0f)
                )

                "Atmospheric pressure" -> chartTopic.entryList.add(
                    Entry(index.toFloat(), item.main?.pressure?.toFloat() ?: 0f)
                )

                "Cloudiness" -> chartTopic.barEntryList!!.add(
                    BarEntry(index.toFloat(), item.clouds?.all?.toFloat() ?: 0f)
                )

                "Wind speed" -> chartTopic.entryList.add(
                    Entry(index.toFloat(), item.wind?.speed?.toFloat() ?: 0f)
                )
            }
        }

        setDescriptionText(chartTopic.description)
        setDataChart(chartTopic)
    }


    private fun setDescriptionText(title: String) {
        description.text = title
    }

    private fun setDataChart(chartTopic: ChartTopic) {
        showChart()

        lineData = LineData(LineDataSet(emptyEntries, ""))
        barData = BarData(BarDataSet(emptyBarEntries, ""))

        if (chartTopic.chartType == "line") {
            when (chartTopic.topicChart) {
                "Temperature" -> setYAxisAndDataSet(0f, 50f, CelsiusValueFormatter(), chartTopic)
                "Atmospheric pressure" -> setYAxisAndDataSet(
                    970f,
                    1030f,
                    PressureValueFormatter(),
                    chartTopic
                )

                "Wind speed" -> setYAxisAndDataSet(0f, 20f, SpeedValueFormatter(), chartTopic)
            }
            lineDataSet.apply {
                color = Color.RED
                lineWidth = 3f
            }

            lineData = LineData(lineDataSet)
        } else {
            setYAxisAndDataSet(0f, 100f, MyValueFormatter(), chartTopic)
            yAxis.axisLineWidth = 2f

            barDataSet.color = Color.BLUE

            barData = BarData(barDataSet)
        }
        combinedData.setData(lineData)
        combinedData.setData(barData)

        binding.mainChart.data = combinedData
        binding.mainChart.invalidate()
    }

    private fun setYAxisAndDataSet(
        minimum: Float,
        maximum: Float,
        valueFormatter: ValueFormatter,
        chartTopic: ChartTopic
    ) {
        yAxis.apply {
            axisMinimum = minimum
            axisMaximum = maximum
            setValueFormatter(valueFormatter)
        }

        when (chartTopic.topicChart) {
            "Temperature", "Atmospheric pressure", "Wind speed" -> {
                lineDataSet = LineDataSet(chartTopic.entryList, chartTopic.topicChart)
            }
            else -> {
                barDataSet = BarDataSet(chartTopic.barEntryList, chartTopic.topicChart)
            }
        }
    }
}
package com.example.weatherapp.model

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry

data class ChartTopic (
    val description: String,
    val entryList: MutableList<Entry>,
    val barEntryList: MutableList<BarEntry>? = null,
    val chartType: String,
    val topicChart: String
)
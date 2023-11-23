package com.example.weatherapp.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.DetailForecastItemBinding
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DetailForecastAdapter(private var listDetails: List<com.example.weatherapp.model.List>) :
    RecyclerView.Adapter<DetailForecastAdapter.DetailForecastViewHolder>() {
    inner class DetailForecastViewHolder(val detailForecastItemBinding: DetailForecastItemBinding) :
        RecyclerView.ViewHolder(detailForecastItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailForecastViewHolder {
        return DetailForecastViewHolder(
            DetailForecastItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listDetails.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DetailForecastViewHolder, position: Int) {
        val item = listDetails[position]

        holder.detailForecastItemBinding.apply {
            Picasso.get()
                .load("https://openweathermap.org/img/w/${item.weather[0].icon}.png")
                .into(imageItem)
            textHour.text = item.dt?.let { convertUnixTimestamp(it.toLong()) }
            textTemp.text = "${item.main?.temp?.let { kelvinToCelsius(it) }}°C"
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertUnixTimestamp(unixTimestamp: Long): String {
        val instant = Instant.ofEpochSecond(unixTimestamp)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        val currentDateTime = LocalDateTime.now()

        return if (dateTime.toLocalDate() == currentDateTime.toLocalDate()) {
            dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            dateTime.format(DateTimeFormatter.ofPattern("HH:mm dd/MM"))
        }
    }

    private fun kelvinToCelsius(kelvin: Double): Int {
        val celsius = kelvin - 273.15

        return celsius.toInt()
    }
}
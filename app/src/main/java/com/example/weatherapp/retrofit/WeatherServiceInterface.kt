package com.example.weatherapp.retrofit

import com.example.weatherapp.model.ForecastData
import com.example.weatherapp.model.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceInterface {
    @GET("data/2.5/weather")
    fun getWeather(
        @Query("lat") latitude: Any,
        @Query("lon") longitude: Any,
        @Query("appid") apiKey: String
    ): Call<WeatherData>

    @GET("data/2.5/forecast")
    fun getForecast(
        @Query("lat") latitude: Any,
        @Query("lon") longitude: Any,
        @Query("appid") apiKey: String
    ): Call<ForecastData>
}
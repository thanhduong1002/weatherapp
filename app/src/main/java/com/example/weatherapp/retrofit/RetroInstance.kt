package com.example.weatherapp.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroInstance {
    private const val BASE_URL = "https://api.openweathermap.org/"
    private const val BASE_URLProvince = "https://provinces.open-api.vn/"

    val instance: WeatherServiceInterface by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(WeatherServiceInterface::class.java)
    }

    val instanceProvince: ProvinceServiceInterface by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URLProvince)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ProvinceServiceInterface::class.java)
    }
}
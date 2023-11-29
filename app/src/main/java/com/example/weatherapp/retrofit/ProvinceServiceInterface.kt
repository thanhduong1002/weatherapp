package com.example.weatherapp.retrofit

import com.example.weatherapp.model.ProvinceData
import retrofit2.Call
import retrofit2.http.GET

interface ProvinceServiceInterface {
    @GET("api/p")
    fun getListProvinces(): Call<List<ProvinceData>>
}
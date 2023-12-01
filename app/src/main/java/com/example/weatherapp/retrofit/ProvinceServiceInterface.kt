package com.example.weatherapp.retrofit

import com.example.weatherapp.model.DetailProvince
import com.example.weatherapp.model.ProvinceData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProvinceServiceInterface {
    @GET("api/p")
    fun getListProvinces(): Call<List<ProvinceData>>

    @GET("api/p/{code}")
    fun  getDetailProvinceByCode(@Path("code") code: Int, @Query("depth") depth: Int): Call<DetailProvince>
}
package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.retrofit.RetroInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel: ViewModel() {
    var weatherLiveData: MutableLiveData<WeatherData> = MutableLiveData()

    fun callWeatherAPI(lat: Any, lon: Any, apikey: String) {
        RetroInstance.instance.getWeather(lat, lon, apikey).enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()

                    weatherLiveData.postValue(weatherResponse)
                } else {
                    val weatherResponse = response.body()

                    Log.d("Failed", "Failed: $weatherResponse")
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                weatherLiveData.postValue(null)

                Log.d("API_CALL", "API call failed. Throwable: ${t.message}")
            }
        })
    }
}
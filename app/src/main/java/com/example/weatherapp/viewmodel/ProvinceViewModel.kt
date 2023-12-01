package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.DetailProvince
import com.example.weatherapp.model.Districts
import com.example.weatherapp.model.ProvinceData
import com.example.weatherapp.retrofit.RetroInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProvinceViewModel : ViewModel() {
    var provinceLiveData: MutableLiveData<List<ProvinceData>> = MutableLiveData()
    var listDistrictsLiveData: MutableLiveData<List<Districts>> = MutableLiveData()
    fun callProvinceAPI() {
        RetroInstance.instanceProvince.getListProvinces().enqueue(object : Callback<List<ProvinceData>> {
            override fun onResponse(call: Call<List<ProvinceData>>, response: Response<List<ProvinceData>>) {
                if (response.isSuccessful) {
                    val provincesResponse = response.body()

                    provinceLiveData.postValue(provincesResponse)
                } else {
                    val provincesResponse = response.body()

                    Log.d("Failed", "Failed: $provincesResponse")
                }
            }

            override fun onFailure(call: Call<List<ProvinceData>>, t: Throwable) {
                provinceLiveData.postValue(null)

                Log.d("API_CALL", "API call failed. Throwable: ${t.message}")
            }
        })
    }

    fun getDetailProvinceByCode(code: Int, dept: Int) {
        RetroInstance.instanceProvince.getDetailProvinceByCode(code, dept).enqueue(object : Callback<DetailProvince> {
            override fun onResponse(call: Call<DetailProvince>, response: Response<DetailProvince>) {
                if (response.isSuccessful) {
                    val detailProvincesResponse = response.body()

                    listDistrictsLiveData.postValue(detailProvincesResponse.districts)
                    Log.d("Success", "Success: $detailProvincesResponse")
                } else {
                    val detailProvincesResponse = response.body()

                    Log.d("Failed", "Failed: $detailProvincesResponse")
                }
            }

            override fun onFailure(call: Call<DetailProvince>, t: Throwable) {
                listDistrictsLiveData.postValue(null)

                Log.d("API_CALL", "API call failed. Throwable: ${t.message}")
            }
        })
    }
}
package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class ProvinceData (

    @SerializedName("name"          ) var name         : String?           = null,
    @SerializedName("code"          ) var code         : Int?              = null,
    @SerializedName("division_type" ) var divisionType : String?           = null,
    @SerializedName("codename"      ) var codename     : String?           = null,
    @SerializedName("phone_code"    ) var phoneCode    : Int?              = null,
    @SerializedName("districts"     ) var districts    : ArrayList<String> = arrayListOf()

)
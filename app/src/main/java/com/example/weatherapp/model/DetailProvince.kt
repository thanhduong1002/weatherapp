package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class DetailProvince (

    @SerializedName("name"          ) var name         : String?              = null,
    @SerializedName("code"          ) var code         : Int?                 = null,
    @SerializedName("division_type" ) var divisionType : String?              = null,
    @SerializedName("codename"      ) var codename     : String?              = null,
    @SerializedName("phone_code"    ) var phoneCode    : Int?                 = null,
    @SerializedName("districts"     ) var districts    : ArrayList<Districts> = arrayListOf()

)

data class Districts (

    @SerializedName("name"          ) var name         : String?           = null,
    @SerializedName("code"          ) var code         : Int?              = null,
    @SerializedName("division_type" ) var divisionType : String?           = null,
    @SerializedName("codename"      ) var codename     : String?           = null,
    @SerializedName("province_code" ) var provinceCode : Int?              = null,
    @SerializedName("wards"         ) var wards        : ArrayList<String> = arrayListOf()

)
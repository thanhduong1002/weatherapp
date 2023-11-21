package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class ForecastData (
    @SerializedName("cod"     ) var cod     : String?         = null,
    @SerializedName("message" ) var message : Int?            = null,
    @SerializedName("cnt"     ) var cnt     : Int?            = null,
    @SerializedName("list"    ) var list    : ArrayList<List> = arrayListOf(),
    @SerializedName("city"    ) var city    : City?           = City()
)

data class List (

    @SerializedName("dt"         ) var dt         : Int?               = null,
    @SerializedName("main"       ) var main       : Main?              = Main(),
    @SerializedName("weather"    ) var weather    : ArrayList<Weather> = arrayListOf(),
    @SerializedName("clouds"     ) var clouds     : Clouds?            = Clouds(),
    @SerializedName("wind"       ) var wind       : Wind?              = Wind(),
    @SerializedName("visibility" ) var visibility : Int?               = null,
    @SerializedName("pop"        ) var pop        : Double?            = null,
    @SerializedName("sys"        ) var sys        : Sys?               = Sys(),
    @SerializedName("dt_txt"     ) var dtTxt      : String?            = null

)

data class City (

    @SerializedName("id"         ) var id         : Int?    = null,
    @SerializedName("name"       ) var name       : String? = null,
    @SerializedName("country"    ) var country    : String? = null,
    @SerializedName("population" ) var population : Int?    = null,
    @SerializedName("timezone"   ) var timezone   : Int?    = null,
    @SerializedName("sunrise"    ) var sunrise    : Int?    = null,
    @SerializedName("sunset"     ) var sunset     : Int?    = null

)
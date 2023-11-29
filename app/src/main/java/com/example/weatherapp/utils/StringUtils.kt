package com.example.weatherapp.utils

object StringUtils {
    fun removeTinh(inputString: String): String {
        val resultString = inputString.replace("tỉnh", "", ignoreCase = true)

        return resultString.trim()
    }
}
package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private var currentDateString: String = ""
    private val daNangDistricts = arrayOf(
        arrayOf("Hoa Vang", 16.083, 108.0),
        arrayOf("Lien Chieu", 16.0944, 108.1742),
        arrayOf("Thanh Khe", 16.0678, 108.1870),
        arrayOf("Ngu Hanh Son", 16.0590, 108.2448),
        arrayOf("Son Tra", 16.0820, 108.2244)
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locations = arrayOf("Hoa Vang", "Lien Chieu", "Thanh Khe", "Ngu Hanh Son", "Son Tra")

        val adapter = ArrayAdapter(this, R.layout.text_dropdown, locations)
        adapter.setDropDownViewResource(R.layout.spinner_text_dropdown)
        binding.spinnerLocation.adapter = adapter
        binding.spinnerLocation.setPopupBackgroundResource(R.drawable.gradient_bg)

        binding.spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLocation = locations[position]

                val selectedDistrict = daNangDistricts.firstOrNull { it[0] == selectedLocation }

                if (selectedDistrict != null) {
                    val lat = selectedDistrict[1]
                    val lon = selectedDistrict[2]

                    weatherViewModel.callWeatherAPI(lat, lon, getString(R.string.apikey))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        weatherViewModel.weatherLiveData.observe(this) {weather ->
            binding.status.text = "${weather.weather[0].description?.let { capitalizeEachWord(it) }}"
            Picasso.get().load("https://openweathermap.org/img/w/${weather.weather[0].icon}.png").into(binding.icon)
            binding.temp.text = "${weather.main?.temp?.let { kelvinToCelsius(it) }}°C"
            binding.textMinTemp.text = "Min temp: ${weather.main?.tempMin?.let { kelvinToCelsius(it) }}°C"
            binding.textMaxTemp.text = "Max temp: ${weather.main?.tempMax?.let { kelvinToCelsius(it) }}°C"
            binding.pressure.text = "${weather.main?.pressure} hPa"
            binding.humidity.text = "${weather.main?.humidity}%"
            binding.wind.text = "${weather.wind?.speed} m/s"
            binding.sunrise.text = "${weather.sys?.sunrise?.toLong()
                ?.let { convertUnixTimestampToTime(it) }}"
            binding.sunset.text = "${weather.sys?.sunset?.toLong()
                ?.let { convertUnixTimestampToTime(it) }}"
            binding.textUpdate?.text = "Update at: $currentDateString"
        }

        weatherViewModel.callWeatherAPI(16.083, 108.0, getString(R.string.apikey))

        binding.textInfo.setOnClickListener {
            showPopupDialog()
            Log.d("showPopupDialog", "showPopupDialog: ")
        }
        Log.d("onCreate", "onCreate: Completed")
    }

    private fun kelvinToCelsius(kelvin: Double): Double {
        val celsius = kelvin - 273.15
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(celsius).toDouble()
    }

    private fun convertUnixTimestampToTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(timestamp * 1000)

        return dateFormat.format(date)
    }

    private fun capitalizeEachWord(sentence: String): String {
        val words = sentence.split(" ")
        val capitalizedWords = words.map { word ->
            word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
        }
        return capitalizedWords.joinToString(" ")
    }


    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDateTime() {
        val currentDate = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        Log.d("currentDate", "currentDate: ${formatter.format(currentDate)}")
        currentDateString = formatter.format(currentDate)
    }

    private fun showPopupDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val radioGroup: RadioGroup = dialog.findViewById(R.id.radioGroup)
        val buttonOK: Button = dialog.findViewById(R.id.buttonOK)

        buttonOK.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = dialog.findViewById<RadioButton>(selectedRadioButtonId)
                val selectedText = selectedRadioButton.text.toString()

                if (selectedText == getString(R.string._5_days)) {
                    val intent = Intent(this, StatictisActivity::class.java)

                    startActivity(intent)
                }
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
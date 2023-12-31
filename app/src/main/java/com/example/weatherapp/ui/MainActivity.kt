package com.example.weatherapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.data.DataDistricts
import com.example.weatherapp.R
import com.example.weatherapp.adapter.DetailForecastAdapter
import com.example.weatherapp.adapter.ProvincesAdapter
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.interfaces.IChooseProvince
import com.example.weatherapp.model.LatLonDistrict
import com.example.weatherapp.model.ProvinceData
import com.example.weatherapp.utils.StringUtils
import com.example.weatherapp.viewmodel.ProvinceViewModel
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), IChooseProvince {
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var provinceViewModel: ProvinceViewModel
    private lateinit var detailForecastAdapter: DetailForecastAdapter
    private lateinit var provincesAdapter: ProvincesAdapter
    private val daNangDistricts = arrayOf(
        arrayOf("Hoa Vang", 16.083, 108.0),
        arrayOf("Lien Chieu", 16.0944, 108.1742),
        arrayOf("Thanh Khe", 16.0678, 108.1870),
        arrayOf("Ngu Hanh Son", 16.0590, 108.2448),
        arrayOf("Son Tra", 16.0820, 108.2244)
    )
    private lateinit var listLatLonDistrict: List<LatLonDistrict>
    private var listNameProvince: List<String?> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLocationSpinner(emptyList())
        listLatLonDistrict = DataDistricts().loadDataDistricts()

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        provinceViewModel = ViewModelProvider(this)[ProvinceViewModel::class.java]

        observeWeatherLiveData()
        observeForecastLiveData()
        observeProvincesLiveData()
        observeListDistrictsAndNameProvinceLiveData()

        setupInitialWeatherAndForecast()
        setupRecyclerViewProvinces()

        binding.apply {
            seeMoreLinear.setOnClickListener {
                showPopupDialog()
            }

            switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
                relativeNoFound.visibility = View.INVISIBLE
                recyclerViewProvinces.visibility = View.INVISIBLE
            }

            searchView.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    filterListProvinces(p0)
                    binding.recyclerViewProvinces.visibility = View.VISIBLE
                    return true
                }
            })
        }
    }

    private fun setupLocationSpinner(listDistricts: List<String?>) {
        val locations = listOf("Hoa Vang", "Lien Chieu", "Thanh Khe", "Ngu Hanh Son", "Son Tra")

        val adapter = if (listDistricts.isEmpty()) {
            ArrayAdapter(this, R.layout.text_dropdown, locations)
        } else {
            ArrayAdapter(this, R.layout.text_dropdown, listDistricts)
        }

        adapter.setDropDownViewResource(R.layout.spinner_text_dropdown)

        binding.spinnerLocation.apply {
            setAdapter(adapter)
            setPopupBackgroundResource(R.color.black)
            onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (listDistricts.isNotEmpty()) {
                            val selectedLocation = listDistricts[position]

                            listLatLonDistrict.find { it.name == selectedLocation }
                                ?.let { district ->
                                    weatherViewModel.apply {
                                        callWeatherAPI(
                                            district.lat,
                                            district.lon,
                                            getString(R.string.apikey)
                                        )
                                        callForecastAPI(
                                            district.lat,
                                            district.lon,
                                            getString(R.string.apikey)
                                        )
                                    }
                                }
                        } else {
                            val selectedLocation = locations[position]
                            val selectedDistrict =
                                daNangDistricts.firstOrNull { it[0] == selectedLocation }

                            selectedDistrict?.let {
                                val lat = selectedDistrict[1]
                                val lon = selectedDistrict[2]

                                weatherViewModel.apply {
                                    callWeatherAPI(lat, lon, getString(R.string.apikey))
                                    callForecastAPI(lat, lon, getString(R.string.apikey))
                                }
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
        }
    }

    private fun setupRecyclerViewProvinces() {
        provinceViewModel.callProvinceAPI()

        provincesAdapter = ProvincesAdapter(emptyList(), this)
        binding.recyclerViewProvinces.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = provincesAdapter
        }
    }

    private fun observeProvincesLiveData() {
        provinceViewModel.provinceLiveData.observe(this) { provinces ->
            if (provinces != null) {
                provincesAdapter = ProvincesAdapter(provinces, this)
                binding.recyclerViewProvinces.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = provincesAdapter
                }
            } else {
                Log.d("provinces", "provinces: null")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeWeatherLiveData() {
        weatherViewModel.weatherLiveData.observe(this) { weather ->
            if (weather != null) {
                binding.apply {
                    status.text = weather.weather[0].description?.let { capitalizeEachWord(it) }
                    Picasso.get()
                        .load("https://openweathermap.org/img/w/${weather.weather[0].icon}.png")
                        .into(icon)
                    temp.text = "${weather.main?.temp?.let { kelvinToCelsius(it) }}°C"
                    textMinTemp.text =
                        "Min temp: ${weather.main?.tempMin?.let { kelvinToCelsius(it) }}°C"
                    textMaxTemp.text =
                        "Max temp: ${weather.main?.tempMax?.let { kelvinToCelsius(it) }}°C"
                    pressure.text = "${weather.main?.pressure} hPa"
                    humidity.text = "${weather.main?.humidity}%"
                    wind.text = "${weather.wind?.speed} m/s"
                    sunrise.text =
                        "${weather.sys?.sunrise?.toLong()?.let { convertUnixTimestampToTime(it) }}"
                    sunset.text =
                        "${weather.sys?.sunset?.toLong()?.let { convertUnixTimestampToTime(it) }}"
                    textUpdate.text = "Update at: ${getCurrentDateTime()}"
                }
            }
        }
    }

    private fun observeForecastLiveData() {
        weatherViewModel.forecastLiveData.observe(this) { forecast ->
            if (forecast != null) {
                detailForecastAdapter = DetailForecastAdapter(forecast)
                binding.detailForecastRecycler.apply {
                    layoutManager =
                        LinearLayoutManager(
                            this@MainActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                    adapter = detailForecastAdapter
                }
            }
        }
    }

    private fun observeListDistrictsAndNameProvinceLiveData() {
        provinceViewModel.listDistrictsLiveData.observe(this) { list ->
            setupLocationSpinner(list.map { it.name })
        }

        provinceViewModel.nameProvinceLiveData.observe(this) {list ->
            listNameProvince = list
        }
    }

    private fun setupInitialWeatherAndForecast() {
        weatherViewModel.apply {
            callWeatherAPI(16.083, 108.0, getString(R.string.apikey))
            callForecastAPI(16.083, 108.0, getString(R.string.apikey))
        }

        detailForecastAdapter = DetailForecastAdapter(emptyList())
        binding.detailForecastRecycler.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = detailForecastAdapter
        }
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
    private fun getCurrentDateTime(): String {
        val currentDate = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return formatDateTime(formatter.format(currentDate))
    }

    private fun showPopupDialog() {
        val dialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.custom_popup)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val radioGroup: RadioGroup = dialog.findViewById(R.id.radioGroup)
        val buttonOK: Button = dialog.findViewById(R.id.buttonOK)

        val radioButtonToday: RadioButton? = dialog.findViewById(R.id.radioButtonToday)
        radioButtonToday?.isChecked = true

        buttonOK.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = dialog.findViewById<RadioButton>(selectedRadioButtonId)
                val selectedText = selectedRadioButton.text.toString()

                if (selectedText == getString(R.string._5_days)) {
                    Intent(this, StatictisActivity::class.java).apply {
                        startActivity(this)
                    }
                } else if (selectedText == getString(R.string.statistic_forecast)) {
                    Intent(this, StatisticForecastActivity::class.java).apply {
                        startActivity(this)
                    }
                }
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun formatDateTime(inputDateTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a dd/MM/yyyy", Locale.getDefault())

        try {
            val date = inputFormat.parse(inputDateTime)

            return outputFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    private fun filterListProvinces(newText: String?) {
        if (newText != null) {
            val filteredList = ArrayList<ProvinceData>()
            for (i in provinceViewModel.provinceLiveData.value!!) {
                if (i.name?.let {
                        StringUtils.removeTinh(it).lowercase(Locale.ROOT).contains(newText)
                    } == true) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty() && !listNameProvince.any { it?.contains(newText)!! }) {
                binding.relativeNoFound.visibility = View.VISIBLE
            } else {
                provincesAdapter.setListProvince(filteredList.toList())
                binding.relativeNoFound.visibility = View.INVISIBLE
            }
        }
    }

    override fun onClickProvince(nameProvince: String, codeProvince: Int) {
        provinceViewModel.getDetailProvinceByCode(codeProvince, 2)
        binding.apply {
            searchView.setQuery(StringUtils.removeTinh(nameProvince), false)
            recyclerViewProvinces.visibility = View.INVISIBLE
            relativeNoFound.visibility = View.INVISIBLE
        }
        hideKeyboard(this)
    }

    private fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val view = activity.currentFocus

        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
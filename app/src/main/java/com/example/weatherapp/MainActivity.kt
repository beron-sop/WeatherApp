package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.RetrofitClient
import com.example.weatherapp.network.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var tvTemperature: TextView
    private lateinit var tvDescription: TextView
    private lateinit var ivWeatherIcon: ImageView
    private lateinit var etCity: EditText
    private lateinit var btnSearch: Button

    private val weatherService: WeatherService by lazy {
        RetrofitClient.weatherService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTemperature = findViewById(R.id.tvTemperature)
        tvDescription = findViewById(R.id.tvDescription)
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon)
        etCity = findViewById(R.id.etCity)
        btnSearch = findViewById(R.id.btnSearch)

        // 🌟 ADDED: Initialize TextViews with blank or placeholder text 🌟
        tvTemperature.text = "Temperature: --°C"
        tvDescription.text = "Description: N/A"
        ivWeatherIcon.setImageDrawable(null) // Clear any initial image


        btnSearch.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchWeather(city)
            } else {
                Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(city: String) {
        val apiKey = "210ef1e68cc6f1f145626714154c7551"
        val call = weatherService.getCurrentWeather(city, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    Log.d("API_TEST", "Full response: $weatherResponse")

                    if (weatherResponse != null) {
                        val temperature = weatherResponse.main.temp
                        val description = weatherResponse.weather[0].description

                        tvTemperature.text = "Temperature: $temperature°C"
                        tvDescription.text = description

                        val iconCode = weatherResponse.weather[0].icon
                        val iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png"

                        Glide.with(this@MainActivity)
                            .load(iconUrl)
                            .into(ivWeatherIcon)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "No data received from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("API_TEST", "API error: ${response.code()} - ${response.message()}")

                    // 🌟 ADDED: Clear data on error 🌟
                    tvTemperature.text = "Temperature: Error"
                    tvDescription.text = "Error fetching data"
                    ivWeatherIcon.setImageDrawable(null)

                    Toast.makeText(
                        this@MainActivity,
                        "API Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("API_TEST", "API call failed", t)

                tvTemperature.text = "Temperature: Failed"
                tvDescription.text = "Connection Failed"
                ivWeatherIcon.setImageDrawable(null)

                Toast.makeText(
                    this@MainActivity,
                    "Failed: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
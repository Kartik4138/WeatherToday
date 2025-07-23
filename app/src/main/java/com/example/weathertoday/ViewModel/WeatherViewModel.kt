package com.example.weathertoday.ViewModel

import androidx.lifecycle.ViewModel
import com.example.weathertoday.Repository.WeatherRepository
import com.example.weathertoday.Server.ApiClient
import com.example.weathertoday.Server.ApiServices
import retrofit2.create

class WeatherViewModel(val repository: WeatherRepository): ViewModel() {

    constructor() : this(WeatherRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCurrentWeather(lat: Double, lng: Double, unit: String) =
        repository.getCurrentWeather(lat, lng, unit)

    fun loadForecastWeather(lat: Double, lng: Double, unit: String) =
        repository.getForecastWeather(lat, lng, unit)
}
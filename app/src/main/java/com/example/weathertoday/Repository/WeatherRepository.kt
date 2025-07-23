package com.example.weathertoday.Repository

import com.example.weathertoday.Server.ApiServices

class WeatherRepository(val api: ApiServices) {

    fun getCurrentWeather(lat: Double,lng: Double, unit:String)=
        api.getCurrentWeather(lat,lng,unit,"fee4a34d283870e4ee40af8ba9f78ddf")

    fun getForecastWeather(lat: Double,lng: Double, unit:String)=
        api.getForecastWeather(lat,lng,unit,"fee4a34d283870e4ee40af8ba9f78ddf")
}
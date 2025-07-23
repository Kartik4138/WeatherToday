package com.example.weathertoday.Repository

import com.example.weathertoday.Server.ApiServices

class CityRepository(val api: ApiServices) {
    fun getCities(q:String,limit:Int)=
        api.getCitiesList(q,limit,"fee4a34d283870e4ee40af8ba9f78ddf")
}
package com.example.weathertoday.ViewModel

import androidx.lifecycle.ViewModel
import com.example.weathertoday.Repository.CityRepository
import com.example.weathertoday.Server.ApiClient
import com.example.weathertoday.Server.ApiServices

class CityViewModel(val repository: CityRepository) : ViewModel() {
    constructor():this(CityRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCity(q:String, limit: Int) =
        repository.getCities(q, limit)
}
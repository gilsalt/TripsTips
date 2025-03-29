package com.tripstips.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tripstips.app.model.WeatherResponse
import com.tripstips.app.repos.WeatherRepository

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> get() = _weatherData

    fun fetchWeather(city: String, apiKey: String) {
        repository.getWeather(city, apiKey) { response ->
            _weatherData.postValue(response)
        }
    }
}

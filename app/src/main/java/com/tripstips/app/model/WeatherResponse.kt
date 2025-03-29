package com.tripstips.app.model

data class WeatherResponse(
    val main: Main,
    val weather: List<WeatherDescription>,
    val name: String
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class WeatherDescription(
    val description: String
)


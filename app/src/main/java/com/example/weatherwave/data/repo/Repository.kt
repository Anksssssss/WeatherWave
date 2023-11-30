package com.example.weatherwave.data.repo

import com.example.weatherwave.data.api.Resource
import com.example.weatherwave.data.api.WeatherApiService
import com.example.weatherwave.data.db.WeatherDao
import com.example.weatherwave.data.db.WeatherDetail
import com.example.weatherwave.data.models.WeatherData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class Repository():BaseRepo() {

    private val weatherApi = WeatherApiService.api
    suspend fun getCurrentWeather(
        lat: String,
        lon: String,
        appid: String
    ):Resource<WeatherData>{
        return safeApiCall { weatherApi.getCurrentWeather(lat,lon,appid) }
    }

    suspend fun getCurrentWeatherOf(
        cityName: String,
        appid: String
    ):Resource<WeatherData>{
        return safeApiCall { weatherApi.getCurrentWeatherOf(cityName,appid) }
    }
}
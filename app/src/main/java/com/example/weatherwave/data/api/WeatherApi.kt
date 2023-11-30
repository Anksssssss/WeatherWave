package com.example.weatherwave.data.api

import com.example.weatherwave.data.models.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather?")
    suspend fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") long: String,
        @Query("appid") key: String,
        @Query("units") unit: String = "metric"
    ):Response<WeatherData>

    @GET("data/2.5/weather?")
    suspend fun getCurrentWeatherOf(
        @Query("q") cityNAme : String,
        @Query("appid") key : String,
        @Query("units") unit: String = "metric"
    ):Response<WeatherData>
}
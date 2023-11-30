package com.example.weatherwave.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WeatherInfo")
data class WeatherDetail(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var lat: Double? = null,
    var lon: Double? = null,
    var weatherDesc: String? = null,
    var temp: Double? = null,
    var tempMin: Double? = null,
    var tempMax: Double? = null,
    var tempfeelsLike: Double? = null,
    var humidity: Int? = null,
    var windSpeed: Double? = null,
    var country: String? = null,
    var place: String? = null,
    var time: String? = null,

)

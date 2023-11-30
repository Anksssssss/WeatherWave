package com.example.weatherwave.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherDetail: WeatherDetail)

    @Query("DELETE FROM WeatherInfo")
    fun deleteAll();

    @Query("SELECT * FROM WeatherInfo")
    fun getWeatherData() : List<WeatherDetail>
}
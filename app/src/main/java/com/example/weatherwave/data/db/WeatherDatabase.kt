package com.example.weatherwave.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WeatherDetail::class],version = 1)
abstract class WeatherDatabase: RoomDatabase() {
    abstract fun weatherDao():WeatherDao

    companion object{
        @Volatile
        var INSTANCE :WeatherDatabase? = null

        @Synchronized
        fun getInstance(context : Context):WeatherDatabase{
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    WeatherDatabase::class.java,
                    "weather_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE as WeatherDatabase
        }
    }
}
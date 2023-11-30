package com.example.weatherwave.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwave.data.api.Resource
import com.example.weatherwave.data.api.WeatherApiService
import com.example.weatherwave.data.db.WeatherDao
import com.example.weatherwave.data.db.WeatherDatabase
import com.example.weatherwave.data.db.WeatherDetail
import com.example.weatherwave.data.models.WeatherData
import com.example.weatherwave.data.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class WeatherViewModel(): ViewModel() {

    private val repository = Repository()
    var latitude: String? = null
    var longitude: String? = null
    val weatherLiveData = MutableLiveData<Resource<WeatherData>>()
    val newYorkLiveData = MutableLiveData<Resource<WeatherData>>()
    val singaporeLiveData = MutableLiveData<Resource<WeatherData>>()
    val mumbaiLiveData = MutableLiveData<Resource<WeatherData>>()
    val delhiLiveData = MutableLiveData<Resource<WeatherData>>()
    val sydneyLiveData = MutableLiveData<Resource<WeatherData>>()
    val melbourneLiveData = MutableLiveData<Resource<WeatherData>>()

    fun getCurrentWeather(){
        weatherLiveData.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val result = repository.getCurrentWeather(latitude!!,longitude!!,"6ff4c871c965bb798cfb027b6964e014")
                weatherLiveData.postValue(result)
            }catch (e: Exception){
                weatherLiveData.postValue(Resource.Error("Some error occurred"))
            }
        }
    }

    fun getCurrentWeather(city: String, liveData: MutableLiveData<Resource<WeatherData>>) {
        liveData.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getCurrentWeatherOf(city, "6ff4c871c965bb798cfb027b6964e014")
                liveData.postValue(result)
            } catch (e: Exception) {
                liveData.postValue(Resource.Error("Some error occurred"))
            }
        }
    }

    fun getCurrentWeatherOfNewYork() {
        getCurrentWeather("New York", newYorkLiveData)
    }

    fun getCurrentWeatherOfSingapore() {
        getCurrentWeather("Singapore", singaporeLiveData)
    }

    fun getCurrentWeatherOfMumbai() {
        getCurrentWeather("Mumbai", mumbaiLiveData)
    }

    fun getCurrentWeatherOfDelhi() {
        getCurrentWeather("Delhi", delhiLiveData)
    }

    fun getCurrentWeatherOfSydney() {
        getCurrentWeather("Sydney", sydneyLiveData)
    }

    fun getCurrentWeatherOfMelbourne() {
        getCurrentWeather("Melbourne", melbourneLiveData)
    }


//    fun getCurrentWeatherOfNewyork(){
//        newYorkLiveData.postValue(Resource.Loading())
//        viewModelScope.launch(Dispatchers.IO) {
//            try{
//                val result = repository.getCurrentWeatherOf("New York","6ff4c871c965bb798cfb027b6964e014")
//                newYorkLiveData.postValue(result)
//            }catch (e: Exception){
//                newYorkLiveData.postValue(Resource.Error("Some error occurred"))
//            }
//        }
//    }
//
//    fun getCurrentWeatherOfSingapore(){
//        singaporeLiveData.postValue(Resource.Loading())
//        viewModelScope.launch(Dispatchers.IO) {
//            try{
//                val result = repository.getCurrentWeatherOf("Singapore","6ff4c871c965bb798cfb027b6964e014")
//                singaporeLiveData.postValue(result)
//            }catch (e: Exception){
//                singaporeLiveData.postValue(Resource.Error("Some error occurred"))
//            }
//        }
//    }
//
//    fun getCurrentWeatherOfMumbai(){
//        mumbaiLiveData.postValue(Resource.Loading())
//        viewModelScope.launch(Dispatchers.IO) {
//            try{
//                val result = repository.getCurrentWeatherOf("Mumbai","6ff4c871c965bb798cfb027b6964e014")
//                mumbaiLiveData.postValue(result)
//            }catch (e: Exception){
//                mumbaiLiveData.postValue(Resource.Error("Some error occurred"))
//            }
//        }
//    }
//
//    fun getCurrentWeatherOfDelhi(){
//        delhiLiveData.postValue(Resource.Loading())
//        viewModelScope.launch(Dispatchers.IO) {
//            try{
//                val result = repository.getCurrentWeatherOf("Delhi","6ff4c871c965bb798cfb027b6964e014")
//                delhiLiveData.postValue(result)
//            }catch (e: Exception){
//                delhiLiveData.postValue(Resource.Error("Some error occurred"))
//            }
//        }
//    }
//
//    fun getCurrentWeatherOfSydney(){
//        sydneyLiveData.postValue(Resource.Loading())
//        viewModelScope.launch(Dispatchers.IO) {
//            try{
//                val result = repository.getCurrentWeatherOf("Sydney","6ff4c871c965bb798cfb027b6964e014")
//                sydneyLiveData.postValue(result)
//            }catch (e: Exception){
//                sydneyLiveData.postValue(Resource.Error("Some error occurred"))
//            }
//        }
//    }
//
//    fun getCurrentWeatherOfMelbourne(){
//        melbourneLiveData.postValue(Resource.Loading())
//        viewModelScope.launch(Dispatchers.IO) {
//            try{
//                val result = repository.getCurrentWeatherOf("Melbourne","6ff4c871c965bb798cfb027b6964e014")
//                melbourneLiveData.postValue(result)
//            }catch (e: Exception){
//                melbourneLiveData.postValue(Resource.Error("Some error occurred"))
//            }
//        }
//    }




}
package com.example.weatherwave.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherwave.NetworkConnection
import com.example.weatherwave.data.api.Resource
import com.example.weatherwave.data.db.WeatherDao
import com.example.weatherwave.data.db.WeatherDatabase
import com.example.weatherwave.data.db.WeatherDetail
import com.example.weatherwave.data.models.WeatherData
import com.example.weatherwave.databinding.ActivityMainBinding
import com.example.weatherwave.ui.viewModel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var mFusedLocation: FusedLocationProviderClient
    private lateinit var viewModel: WeatherViewModel
    private lateinit var weatherDao: WeatherDao

    override fun onResume() {
        super.onResume()
        if (checkPermission() && locationEnabled() && isInternetAvailable()) {

            weatherDao.deleteAll()
            getLatLon()
            viewModel.getCurrentWeatherOfNewYork()
            viewModel.getCurrentWeatherOfSingapore()
            viewModel.getCurrentWeatherOfMumbai()
            viewModel.getCurrentWeatherOfDelhi()
            viewModel.getCurrentWeatherOfSydney()
            viewModel.getCurrentWeatherOfMelbourne()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val weatherDatabase = WeatherDatabase.getInstance(this)
        weatherDao = weatherDatabase.weatherDao()
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) {
            if (it) {
                weatherDao.deleteAll()
                getLastLocation()
                viewModel.getCurrentWeatherOfNewYork()
                viewModel.getCurrentWeatherOfSingapore()
                viewModel.getCurrentWeatherOfMumbai()
                viewModel.getCurrentWeatherOfDelhi()
                viewModel.getCurrentWeatherOfSydney()
                viewModel.getCurrentWeatherOfMelbourne()
                setObservers()
            } else {
                setData()
            }
        }

        setListeners()


    }

    fun setListeners() {
        binding.refreshBtn.setOnClickListener {
            weatherDao.deleteAll()
            getLatLon()
            viewModel.getCurrentWeatherOfNewYork()
            viewModel.getCurrentWeatherOfSingapore()
            viewModel.getCurrentWeatherOfMumbai()
            viewModel.getCurrentWeatherOfDelhi()
            viewModel.getCurrentWeatherOfSydney()
            viewModel.getCurrentWeatherOfMelbourne()
        }
    }

    private fun setObservers() {
        viewModel.weatherLiveData.observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    binding.currlocPb.visibility = View.INVISIBLE
                    binding.currentLocationCardContent.visibility = View.GONE
                    //binding.currLocationmain.visibility = View.GONE
                    binding.currLocErrorTV.visibility = View.VISIBLE
                }

                is Resource.Loading -> {
                    binding.currlocPb.visibility = View.VISIBLE
                    binding.currentLocationCardContent.visibility = View.GONE
                    //binding.currLocationmain.visibility = View.GONE
                    binding.currLocErrorTV.visibility = View.INVISIBLE
                }

                is Resource.Success -> {
                    binding.currlocPb.visibility = View.INVISIBLE
                    binding.currentLocationCardContent.visibility = View.VISIBLE
                    //binding.currLocationmain.visibility = View.VISIBLE
                    binding.currLocErrorTV.visibility = View.INVISIBLE
                    CoroutineScope(IO).launch {
                        response.data?.let { cacheData(it) }
                    }

                    binding.apply {
                        locationTv.text = response.data!!.name
                        tempTv.text = buildString {
                            append(response.data.main.temp.toInt())
                            append("°C")
                        }
                        if (response.data.weather[0].description.isNotEmpty()) {
                            weatherDescTv.text = response.data.weather[0].description
                        }
                        minTempValueTv.text = buildString {
                            append(response.data.main.temp_min)
                            append("°C")
                        }
                        humidityValueTV.text = buildString {
                            append(response.data.main.humidity)
                            append("%")
                        }
                        maxTempValueTv.text = buildString {
                            append(response.data.main.temp_max)
                            append("°C")
                        }
                        lastupdateTv.text = convertToLocalTimeInCountry(Calendar.getInstance())
                    }

                }
            }
        }
        viewModel.newYorkLiveData.observe(this){ response ->
            when (response) {
                is Resource.Error -> {
                    //binding.progressBar.visibility = View.INVISIBLE
                    binding.newYorkCard.visibility = View.GONE
                   // binding.errorTv.visibility = View.VISIBLE
                }

                is Resource.Loading -> {
                   // binding.progressBar.visibility = View.VISIBLE
                    binding.newYorkCard.visibility = View.GONE
                    //binding.errorTv.visibility = View.INVISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.newYorkCard.visibility = View.VISIBLE
                    binding.errorTv.visibility = View.INVISIBLE
                    CoroutineScope(IO).launch {
                        response.data?.let { cacheData(it) }
                    }

                    binding.apply {
                        locationTvNY.text = response.data!!.name
                        tempTvNY.text = buildString {
                            append("${response.data.main.temp.toInt()}°C")
                        }
                        if (response.data.weather[0].description.isNotEmpty()) {
                            weatherDescTvNY.text = response.data.weather[0].description
                        }
                        minTempValueTvNY.text = buildString {
                            append("${response.data.main.temp_min.toString()}°C")
                        }
                        humidityValueTVNY.text = buildString {
                            append("${response.data.main.humidity.toString()}%")
                        }
                        maxTempValueTvNY.text = buildString {
                            append("${response.data.main.temp_max.toString()}°C")
                        }
                        lastupdateTv.text = convertToLocalTimeInCountry(Calendar.getInstance())
                    }

                }
            }
        }
        viewModel.singaporeLiveData.observe(this){ response ->
            when (response) {
                is Resource.Error -> {
                    //binding.progressBar.visibility = View.INVISIBLE
                    binding.singporeCard.visibility = View.GONE
                   // binding.errorTv.visibility = View.VISIBLE
                }

                is Resource.Loading -> {
                   // binding.progressBar.visibility = View.VISIBLE
                    binding.singporeCard.visibility = View.GONE
                    //binding.errorTv.visibility = View.INVISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.singporeCard.visibility = View.VISIBLE
                    binding.errorTv.visibility = View.INVISIBLE
                    CoroutineScope(IO).launch {
                        response.data?.let { cacheData(it) }
                    }

                    binding.apply {
                        locationTvS.text = response.data!!.name
                        tempTvS.text = buildString {
                            append("${response.data.main.temp.toInt()}°C")
                        }
                        if (response.data.weather[0].description.isNotEmpty()) {
                            weatherDescTvS.text = response.data.weather[0].description
                        }
                        minTempValueTvS.text = buildString {
                            append("${response.data.main.temp_min.toString()}°C")
                        }
                        humidityValueTVS.text = buildString {
                            append("${response.data.main.humidity.toString()}%")
                        }
                        maxTempValueTvS.text = buildString {
                            append("${response.data.main.temp_max.toString()}°C")
                        }
                        lastupdateTv.text = convertToLocalTimeInCountry(Calendar.getInstance())
                    }

                }
            }
        }
        viewModel.mumbaiLiveData.observe(this){ response ->
            when (response) {
                is Resource.Error -> {
                    //binding.progressBar.visibility = View.INVISIBLE
                    binding.mumbaiCard.visibility = View.GONE
                    // binding.errorTv.visibility = View.VISIBLE
                }

                is Resource.Loading -> {
                    // binding.progressBar.visibility = View.VISIBLE
                    binding.mumbaiCard.visibility = View.GONE
                    //binding.errorTv.visibility = View.INVISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.mumbaiCard.visibility = View.VISIBLE
                    binding.errorTv.visibility = View.INVISIBLE
                    CoroutineScope(IO).launch {
                        response.data?.let { cacheData(it) }
                    }

                    binding.apply {
                        locationTvM.text = response.data!!.name
                        tempTvM.text = buildString {
                            append("${response.data.main.temp.toInt()}°C")
                        }
                        if (response.data.weather[0].description.isNotEmpty()) {
                            weatherDescTvM.text = response.data.weather[0].description
                        }
                        minTempValueTvM.text = buildString {
                            append("${response.data.main.temp_min.toString()}°C")
                        }
                        humidityValueTVM.text = buildString {
                            append("${response.data.main.humidity.toString()}%")
                        }
                        maxTempValueTvM.text = buildString {
                            append("${response.data.main.temp_max.toString()}°C")
                        }
                        lastupdateTv.text = convertToLocalTimeInCountry(Calendar.getInstance())
                    }

                }
            }
        }
        viewModel.delhiLiveData.observe(this){ response ->
            when (response) {
                is Resource.Error -> {
                    //binding.progressBar.visibility = View.INVISIBLE
                    binding.delhiCard.visibility = View.GONE
                    // binding.errorTv.visibility = View.VISIBLE
                }

                is Resource.Loading -> {
                    // binding.progressBar.visibility = View.VISIBLE
                    binding.delhiCard.visibility = View.GONE
                    //binding.errorTv.visibility = View.INVISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.delhiCard.visibility = View.VISIBLE
                    binding.errorTv.visibility = View.INVISIBLE
                    CoroutineScope(IO).launch {
                        response.data?.let { cacheData(it) }
                    }

                    binding.apply {
                        locationTvD.text = response.data!!.name
                        tempTvD.text = buildString {
                            append("${response.data.main.temp.toInt()}°C")
                        }
                        if (response.data.weather[0].description.isNotEmpty()) {
                            weatherDescTvD.text = response.data.weather[0].description
                        }
                        minTempValueTvD.text = buildString {
                            append("${response.data.main.temp_min.toString()}°C")
                        }
                        humidityValueTVD.text = buildString {
                            append("${response.data.main.humidity.toString()}%")
                        }
                        maxTempValueTvD.text = buildString {
                            append("${response.data.main.temp_max.toString()}°C")
                        }
                        lastupdateTv.text = convertToLocalTimeInCountry(Calendar.getInstance())
                    }

                }
            }
        }
        viewModel.sydneyLiveData.observe(this){ response ->
            when (response) {
                is Resource.Error -> {
                    //binding.progressBar.visibility = View.INVISIBLE
                    binding.sydneyCard.visibility = View.GONE
                    // binding.errorTv.visibility = View.VISIBLE
                }

                is Resource.Loading -> {
                    // binding.progressBar.visibility = View.VISIBLE
                    binding.sydneyCard.visibility = View.GONE
                    //binding.errorTv.visibility = View.INVISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.sydneyCard.visibility = View.VISIBLE
                    binding.errorTv.visibility = View.INVISIBLE
                    CoroutineScope(IO).launch {
                        response.data?.let { cacheData(it) }
                    }

                    binding.apply {
                        locationTvSy.text = response.data!!.name
                        tempTvSy.text = buildString {
                            append("${response.data.main.temp.toInt()}°C")
                        }
                        if (response.data.weather[0].description.isNotEmpty()) {
                            weatherDescTvSy.text = response.data.weather[0].description
                        }
                        minTempValueTvSy.text = buildString {
                            append("${response.data.main.temp_min.toString()}°C")
                        }
                        humidityValueTVSy.text = buildString {
                            append("${response.data.main.humidity.toString()}%")
                        }
                        maxTempValueTvSy.text = buildString {
                            append("${response.data.main.temp_max.toString()}°C")
                        }
                        lastupdateTv.text = convertToLocalTimeInCountry(Calendar.getInstance())
                    }

                }
            }
        }
        viewModel.melbourneLiveData.observe(this){ response ->
            when (response) {
                is Resource.Error -> {
                    //binding.progressBar.visibility = View.INVISIBLE
                    binding.melbourneCard.visibility = View.GONE
                    // binding.errorTv.visibility = View.VISIBLE
                }

                is Resource.Loading -> {
                    // binding.progressBar.visibility = View.VISIBLE
                    binding.melbourneCard.visibility = View.GONE
                    //binding.errorTv.visibility = View.INVISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.melbourneCard.visibility = View.VISIBLE
                    binding.errorTv.visibility = View.INVISIBLE
                    CoroutineScope(IO).launch {
                        response.data?.let { cacheData(it) }
                    }

                    binding.apply {
                        locationTvMe.text = response.data!!.name
                        tempTvMe.text = buildString {
                            append("${response.data.main.temp.toInt()}°C")
                        }
                        if (response.data.weather[0].description.isNotEmpty()) {
                            weatherDescTvMe.text = response.data.weather[0].description
                        }
                        minTempValueTvMe.text = buildString {
                            append("${response.data.main.temp_min.toString()}°C")
                        }
                        humidityValueTVMe.text = buildString {
                            append("${response.data.main.humidity.toString()}%")
                        }
                        maxTempValueTvMe.text = buildString {
                            append("${response.data.main.temp_max.toString()}°C")
                        }
                        lastupdateTv.text = convertToLocalTimeInCountry(Calendar.getInstance())
                    }

                }
            }
        }
    }


    private fun setData() {
        var weatherData = weatherDao.getWeatherData()
        if (weatherData.isNotEmpty()) {

            binding.mainContent.visibility = View.VISIBLE
            for(d in weatherData){
                if (d.place == "New York"){
                    binding.apply {
                        newYorkCard.visibility = View.VISIBLE
                        locationTvNY.text = d.place
                        tempTvNY.text = buildString { append(d.temp!!.toInt())
                        append("°C")}
                        weatherDescTvNY.text = d.weatherDesc
                        minTempValueTvNY.text = buildString { append(d.temp)
                            append("°C")}
                        humidityValueTVNY.text = buildString { append(d.temp)
                            append("°C")}
                        maxTempValueTvNY.text = buildString { append(d.temp)
                            append("°C")}
                        lastupdateTv.text =  weatherData[0].time
                    }
                }else if(d.place == "Singapore"){
                    binding.apply {
                        singporeCard.visibility = View.VISIBLE
                        locationTvS.text = d.place
                        tempTvS.text = buildString { append(d.temp!!.toInt())
                            append("°C")}
                        weatherDescTvS.text = d.weatherDesc
                        minTempValueTvS.text = buildString { append(d.temp)
                            append("°C")}
                        humidityValueTVS.text = buildString { append(d.temp)
                            append("°C")}
                        maxTempValueTvS.text = buildString { append(d.temp)
                            append("°C")}
                        lastupdateTv.text =  weatherData[0].time
                    }
                }else if(d.place == "Mumbai"){
                    binding.apply {
                        mumbaiCard.visibility = View.VISIBLE
                        locationTvM.text = d.place
                        tempTvM.text = buildString { append(d.temp!!.toInt())
                            append("°C")}
                        weatherDescTvNY.text = d.weatherDesc
                        minTempValueTvM.text = buildString { append(d.temp)
                            append("°C")}
                        humidityValueTVM.text = buildString { append(d.temp)
                            append("°C")}
                        maxTempValueTvM.text = buildString { append(d.temp)
                            append("°C")}
                        lastupdateTv.text =  weatherData[0].time
                    }
                }else if(d.place == "Delhi"){
                    binding.apply {
                        delhiCard.visibility = View.VISIBLE
                        locationTvD.text = d.place
                        tempTvD.text = buildString { append(d.temp!!.toInt())
                            append("°C")}
                        weatherDescTvD.text = d.weatherDesc
                        minTempValueTvD.text = buildString { append(d.temp)
                            append("°C")}
                        humidityValueTVD.text = buildString { append(d.temp)
                            append("°C")}
                        maxTempValueTvD.text = buildString { append(d.temp)
                            append("°C")}
                        lastupdateTv.text =  weatherData[0].time
                    }
                }else if(d.place == "Sydney"){
                    binding.apply {
                        sydneyCard.visibility = View.VISIBLE
                        locationTvSy.text = d.place
                        tempTvSy.text = buildString { append(d.temp!!.toInt())
                            append("°C")}
                        weatherDescTvSy.text = d.weatherDesc
                        minTempValueTvSy.text = buildString { append(d.temp)
                            append("°C")}
                        humidityValueTVSy.text = buildString { append(d.temp)
                            append("°C")}
                        maxTempValueTvSy.text = buildString { append(d.temp)
                            append("°C")}
                        lastupdateTv.text =  weatherData[0].time
                    }
                }else if(d.place == "Melbourne"){
                    binding.apply {
                        melbourneCard.visibility = View.VISIBLE
                        locationTvMe.text = d.place
                        tempTvMe.text = buildString { append(d.temp!!.toInt())
                            append("°C")}
                        weatherDescTvMe.text = d.weatherDesc
                        minTempValueTvMe.text = buildString { append(d.temp)
                            append("°C")}
                        humidityValueTVMe.text = buildString { append(d.temp)
                            append("°C")}
                        maxTempValueTvMe.text = buildString { append(d.temp)
                            append("°C")}
                        lastupdateTv.text =  weatherData[0].time
                    }
                }else{
                    binding.apply {
                        //currLocationmain.visibility = View.VISIBLE
                        currentLocationCardContent.visibility = View.VISIBLE
                        locationTv.text = d.place
                        tempTv.text = buildString { append(d.temp!!.toInt())
                            append("°C")}
                        weatherDescTv.text = d.weatherDesc
                        minTempValueTv.text = buildString { append(d.temp)
                            append("°C")}
                        humidityValueTV.text = buildString { append(d.temp)
                            append("°C")}
                        maxTempValueTv.text = buildString { append(d.temp)
                            append("°C")}
                        lastupdateTv.text =  weatherData[0].time
                    }
                }
            }
        } else {
            binding.errorTv.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
           // binding.mainContent.visibility = View.INVISIBLE
        }
    }

    private suspend fun cacheData(data: WeatherData) {
        //weatherDao.deleteAll()

        var weatherDetail = WeatherDetail()

        weatherDetail.apply {
            lat = data.coord.lat
            lon = data.coord.lon
            weatherDesc = data.weather[0].description
            temp = data.main.temp
            tempMin = data.main.temp_min
            tempMax = data.main.temp_max
            tempfeelsLike = data.main.feels_like
            humidity = data.main.humidity
            windSpeed = data.wind.speed
            country = data.sys.country
            place = data.name
            time = convertToLocalTimeInCountry(Calendar.getInstance())

        }
        weatherDao.insert(weatherDetail)
    }

    fun convertToLocalTimeInCountry(localCalendar: Calendar): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm a")
        return simpleDateFormat.format(localCalendar.time)
    }


    private fun getLastLocation() {
        if (checkPermission()) {
            if (locationEnabled()) {
                getLatLon()
            } else {
                Toast.makeText(this, "Please enable device Location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            RequestPermission()
        }
    }

    private fun getLatLon() {
        if (checkPermission()) {
            mFusedLocation.lastLocation.addOnCompleteListener { task ->
                var location: Location? = task.result
                if (location == null) {
                    Toast.makeText(applicationContext,"Unable to get current location, please refresh", Toast.LENGTH_SHORT).show()
                    NewLocation()
                } else {
                    viewModel.latitude = location.latitude.toString()
                    viewModel.longitude = location.longitude.toString()
                    viewModel.getCurrentWeather()
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun locationEnabled(): Boolean {
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun RequestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(applicationContext, "Location acceses denied", Toast.LENGTH_SHORT) .show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun NewLocation() {
        var locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest.priority =
            com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation: Location? = p0.lastLocation
            Toast.makeText(applicationContext,"location found", Toast.LENGTH_SHORT).show()
            getLatLon()
            //Toast.makeText(applicationContext, "${lastLocation?.latitude} and ${lastLocation?.longitude}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities =
                connectivityManager.getNetworkCapabilities(network)

            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            // For devices below Android M
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }


    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }
}
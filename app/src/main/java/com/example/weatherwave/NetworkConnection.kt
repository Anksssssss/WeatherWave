package com.example.weatherwave

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.lifecycle.LiveData

class NetworkConnection(private val context: Context) :LiveData<Boolean>(){
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var  newtowkConnectionCallback: ConnectivityManager.NetworkCallback?=null

    override fun onActive() {
        super.onActive()
        updateNetworkConnection()
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->{
                registerNetworkCallback()
            }else->{
                context.registerReceiver(networkReciever, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        unregisterNetworkCallback()
    }

    private fun updateNetworkConnection() {
        val networkConnection = connectivityManager.activeNetworkInfo
        postValue(networkConnection?.isConnected==true)
    }

    private val networkReciever = object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            updateNetworkConnection()
        }
    }

    private fun registerNetworkCallback() {
        newtowkConnectionCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(newtowkConnectionCallback!!)
    }

    private fun unregisterNetworkCallback() {
        try {
            newtowkConnectionCallback?.let {
                connectivityManager.unregisterNetworkCallback(it)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}
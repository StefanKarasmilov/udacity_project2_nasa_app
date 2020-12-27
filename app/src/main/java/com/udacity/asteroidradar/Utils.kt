package com.udacity.asteroidradar

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.AsteroidApi
import kotlinx.coroutines.launch

fun networkConnectionAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo

    return networkInfo != null && networkInfo.isConnected
}
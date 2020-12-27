package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.asDatabaseEntity
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.networkConnectionAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainViewModel(val context: Context) : ViewModel() {

    private val database = getDatabase(context)
    private val asteroidsRepository = AsteroidRepository(context, database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _noData = MutableLiveData<Boolean>(false)
    val noData: LiveData<Boolean>
        get() = _noData

    init {
        getPictureOfDayFromApi()

        _asteroids.value = asteroidsRepository.allAsteroids
    }

    private fun getPictureOfDayFromApi() {
        if (networkConnectionAvailable(context)) {
            viewModelScope.launch {
                val apiService = AsteroidApi.retrofitService
                _pictureOfDay.value = apiService.getPictureOfDay()
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

//    private fun showAllAsteroidsFromApi() {
//        viewModelScope.launch {
//            val jsonElement = AsteroidApi.retrofitService.getAllAsteroids()
//            val jsonObject = JSONObject(jsonElement.toString())
//            val asteroids = parseAsteroidsJsonResult(jsonObject)
//            if (!asteroids.isNullOrEmpty()) {
//                _asteroids.value = asteroids
//                _noData.value = false
//            } else {
//                _asteroids.value = asteroids
//                _noData.value = true
//            }
//            asteroidsRepository.refreshAsteroids()
//        }
//    }

    fun showTodayAsteroids() {
        val todayAsteroids = asteroidsRepository.todayAsteroids
        if (!todayAsteroids.isNullOrEmpty()) {
            _asteroids.value = todayAsteroids
            _noData.value = false
        } else {
            _asteroids.value = todayAsteroids
            _noData.value = true
        }
    }

    fun showAllAsteroids() {
        val allAsteroids = asteroidsRepository.allAsteroids
        if (!allAsteroids.isNullOrEmpty()) {
            _asteroids.value = allAsteroids
            _noData.value = false
        } else {
            _asteroids.value = allAsteroids
            _noData.value = true
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
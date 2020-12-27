package com.udacity.asteroidradar.repository

import android.annotation.SuppressLint
import android.content.Context
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.asDatabaseEntity
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.networkConnectionAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AsteroidRepository(private val context: Context, private val database: AsteroidDatabase) {

    @SuppressLint("SimpleDateFormat")
    private val today = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val currentDateTime = LocalDateTime.now()
        currentDateTime.format(DateTimeFormatter.ISO_DATE)
    } else {
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
        val date = Date()
        dateFormat.format(date)
    }


    val allAsteroids = runBlocking(Dispatchers.IO) {
        if (networkConnectionAvailable(context)) {
            refreshAsteroids()
        }
        database.asteroidDao.getAllAsteroids().asDomainModel()
    }

    val todayAsteroids = runBlocking(Dispatchers.IO) {
        if (networkConnectionAvailable(context)) {
            refreshAsteroids()
        }
        database.asteroidDao.getAsteroidsFromToday(today).asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val jsonElement = AsteroidApi.retrofitService.getAllAsteroids()
            val jsonObject = JSONObject(jsonElement.toString())
            val asteroids = parseAsteroidsJsonResult(jsonObject)

            database.asteroidDao.insertAll(*asteroids.asDatabaseEntity())
        }
    }

}
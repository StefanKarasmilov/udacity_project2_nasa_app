package com.udacity.asteroidradar.api

import com.google.gson.JsonElement
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()

interface AsteroidApiService {

    @GET("planetary/apod?api_key=${Constants.API_KEY}")
    suspend fun getPictureOfDay(): PictureOfDay

    @GET("neo/rest/v1/feed?api_key=${Constants.API_KEY}")
    suspend fun getAllAsteroids(): JsonElement

}

object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}
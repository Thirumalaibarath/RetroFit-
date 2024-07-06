package com.example.retrofit

import retrofit2.http.GET

interface SettingsService {
    @GET("/obstacleLimit")
    suspend fun getObstacleLimit(): Map<String, Int>
}
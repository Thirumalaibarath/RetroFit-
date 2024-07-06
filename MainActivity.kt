package com.example.retrofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

var obstacleLimit by mutableIntStateOf(0)

class MainActivity : ComponentActivity() {
    private lateinit var settingsService: SettingsService
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://chasedeux.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            settingsService = retrofit.create(SettingsService::class.java)

            LaunchedEffect(Unit) {
                startDataFetchLoop()
            }

            MainScreen(obstacleLimit = obstacleLimit)
        }
    }

    private fun startDataFetchLoop() {
        scope.launch {
            while (isActive) {
                fetchData()
                delay(100) // Adjust delay as needed (e.g., 5000ms = 5 seconds)
            }
        }
    }

    private suspend fun fetchData() {
        try {
            val response = settingsService.getObstacleLimit()
            obstacleLimit = response["obstacleLimit"] ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel() // Cancel the coroutine when the activity is destroyed
    }
}

@Composable
fun MainScreen(obstacleLimit: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Obstacle Limit: $obstacleLimit", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

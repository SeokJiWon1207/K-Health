package com.example.k_health

import com.example.k_health.food.data.models.FoodResponse
import com.example.k_health.food.data.service.FoodApiService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object Repository {

    var userId = Firebase.auth.currentUser?.uid.orEmpty()

    private val foodApiService: FoodApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create()
    }

    // 호출부
    suspend fun getFoodItems(): List<FoodResponse>? =
        foodApiService.getFoodItems().body()

    // 로깅 체크
    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            ).build()


}

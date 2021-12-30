package com.example.k_health.food.data.service

import com.example.k_health.BuildConfig
import com.example.k_health.food.data.models.FoodResponse
import retrofit2.Response
import retrofit2.http.GET

// Retrofit 1-2) interface로 사용할 메소드 선언
interface FoodApiService {

    @GET("getFoodNtrItdntList?ServiceKey=${BuildConfig.FOOD_SERVICE_KEY}"+
            "&numOfRows=10&pageNo=1&type=json"
    )
    suspend fun getFoodItems(): Response<List<FoodResponse>>
}
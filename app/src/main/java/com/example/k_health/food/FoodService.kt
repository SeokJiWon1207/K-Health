package com.example.k_health.food

import com.example.k_health.BuildConfig
import com.example.k_health.data.Food
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit 1-2) interface로 사용할 메소드 선언
interface FoodService {
    @GET("getFoodNtrItdntList?ServiceKey=${BuildConfig.FOOD_SERVICE_KEY}"+
            "&numOfRows=3&pageNo=1&type=json"
    )

    suspend fun getFood(
        @Query("DESC_KOR") apiKey: String,
    ): Call<List<Food>>
}
package com.example.k_health.food

import com.example.k_health.model.Food
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit 1-2) interface로 사용할 메소드 선언
interface FoodService {
    @GET(
        "JedqWdwYkLIcj44sDhauLIm%2FksihTwWFkEZH4uDVa1%2F9%2FgiS9gG7jbeQrli%2BqEbzLkaLQHLdaSupJUHrBHA%2Fvg%3D%3D"
                + "type=json"
    )
    suspend fun getFood(
        @Query("resultCode") apiKey: String,
    ): Response<Food>
}
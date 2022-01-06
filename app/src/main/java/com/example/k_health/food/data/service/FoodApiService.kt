package com.example.k_health.food.data.service

import com.example.k_health.BuildConfig
import com.example.k_health.food.data.models.Body
import com.example.k_health.food.data.models.FoodResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit 1-2) interface로 사용할 메소드 선언
interface FoodApiService {

    @GET(
        "getFoodNtrItdntList1?serviceKey=${BuildConfig.FOOD_SERVICE_KEY}" +
                "&numOfRows=10" +
                "&pageNo=3" +
                "&type=json"
    )
    suspend fun getFoodItems(): Response<FoodResponse>

    // 사용자의 검색 키워드로 api 요청
    @GET(
        "getFoodNtrItdntList1?serviceKey=${BuildConfig.FOOD_SERVICE_KEY}" +
                "&numOfRows=10+" +
                "&pageNo=3" +
                "&type=json"
    )
    suspend fun getFoodsByName(
        @Query("desc_kor") keyWord: String
    ): Response<FoodResponse>
}
package com.example.k_health.food.data.models


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val items: List<FoodResponse>?,
    @SerializedName("numOfRows")
    val numOfRows: Int?,
    @SerializedName("pageNo")
    val pageNo: Int?,
    @SerializedName("totalCount")
    val totalCount: Int?
)
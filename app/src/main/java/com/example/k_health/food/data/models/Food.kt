package com.example.k_health.food.data.models


import com.google.gson.annotations.SerializedName

data class Food(
    @SerializedName("body")
    val body: FoodSearchDTO?,
    @SerializedName("header")
    val header: Header?
)
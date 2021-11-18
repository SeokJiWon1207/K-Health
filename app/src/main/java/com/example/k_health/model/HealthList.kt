package com.example.k_health.model

data class HealthList(
    val name: String,
    val engName: String,
    val imageUrl: String


) {
    constructor(): this("벤치프레스", "Bench", "사진없음")
}

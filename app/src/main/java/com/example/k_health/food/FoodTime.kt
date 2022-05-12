package com.example.k_health.food

import com.example.k_health.R

enum class FoodTime(val timeImage: Int, val time: String, val textColor: Int) {

    BREAKFAST(R.drawable.ic_sun, "아침식사", R.color.red),

    LUNCH(R.drawable.ic_sun2, "점심식사", R.color.orange),

    DINNER(R.drawable.ic_moon, "저녁식사", R.color.purple),

    ETC(R.drawable.ic_snack, "간식,기타", R.color.peach)
}
package com.example.k_health.food

import androidx.annotation.ColorRes
import com.example.k_health.R

enum class FoodTime(val timeImage: Int, val time: String, @ColorRes val textColor: Int) {

    BREAKFAST(R.drawable.ic_breakfast, "아침식사", R.color.red),

    LUNCH(R.drawable.ic_lunch, "점심식사", R.color.yellow),

    DINNER(R.drawable.ic_dinner, "저녁식사", R.color.purple),

    ETC(R.drawable.ic_etc, "간식,기타", R.color.indigo)
}
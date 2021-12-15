package com.example.k_health.health

import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface TimeInterface {

    fun timeGenerator(): String {
        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        return todayNow
    }
}
package com.example.k_health.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Room 1-1) Entity는 고유 식별자인 PrimaryKey가 반드시 필요하다.
// 큰 의미가 없다면, (autogenerate = true)를 이용해 자동으로 생성도 가능하다.
@Entity
data class History(
    @PrimaryKey val uid: Int?, // 기본키
    @ColumnInfo(name = "keyword") val keyword: String?
)

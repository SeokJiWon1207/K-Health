package com.example.k_health.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

// Retrofit 1-1) REST API로 받아올 데이터를 받아올 모델 클래스 생성
// 서버에서 받는 데이터 모델
// 직렬화 가능한 데이터 클래스
@Parcelize
data class Food(
    // json 데이터의 변수명과 다르게 변수를 짓는 경우
    // @SerializedName("속성명")으로 설정 가능
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultMsg") val resultMsg: String,
    @SerializedName("DESC_KOR") val foodname: String,
    @SerializedName("SERVING_WT") val gram: String,
    @SerializedName("NUTR_CONT1") val kcal: String,
): Parcelable
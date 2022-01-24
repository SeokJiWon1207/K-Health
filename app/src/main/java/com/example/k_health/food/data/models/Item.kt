package com.example.k_health.food.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

// Retrofit 1-1) REST API로 받아올 데이터를 받아올 모델 클래스 생성
// 서버에서 받는 데이터 모델
// 직렬화 가능한 데이터 클래스

@Parcelize
data class Item(
    // json 데이터의 변수명과 다르게 변수를 짓는 경우
    // @SerializedName("속성명")으로 설정 가능
    @SerializedName("ANIMAL_PLANT")
    val aNIMALPLANT: String? = "",
    @SerializedName("BGN_YEAR")
    val bGNYEAR: String? = "",
    @SerializedName("DESC_KOR")
    val foodName: String? = "",
    @SerializedName("NUTR_CONT1")
    val kcal: String? = "",
    @SerializedName("NUTR_CONT2")
    val carbon: String? = "",
    @SerializedName("NUTR_CONT3")
    val protein: String? = "",
    @SerializedName("NUTR_CONT4")
    val fat: String? = "",
    @SerializedName("NUTR_CONT5")
    val sugar: String? = "", // 당
    @SerializedName("NUTR_CONT6")
    val sodium: String? = "", // 나트륨
    @SerializedName("NUTR_CONT7")
    val cholesterol: String? = "", // 콜레스테롤
    @SerializedName("NUTR_CONT8")
    val saturatedFattyAcids: String? = "", // 포화지방산
    @SerializedName("NUTR_CONT9")
    val unsaturatedFattyAcids: String? = "", // 불포화지방산
    @SerializedName("SERVING_WT")
    val gram: String? = "",
    var isSelected: Boolean = false

): Parcelable {
    @JvmName("setSelected1")
    fun setSelected(selected: Boolean) {
        isSelected = selected
    }
}
package com.example.k_health

import com.example.k_health.food.data.models.FoodResponse
import com.example.k_health.food.data.service.FoodApiService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object Repository {

    var userId = Firebase.auth.currentUser?.uid.orEmpty()

    // Retrofit 1-3) Retrofit 인스턴스 생성, 싱글톤의 instance interface 객체 구현
    private val foodApiService: FoodApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.BASE_URL) // baseUrl은 꼭 '/' 로 끝나야 함, 아니면 예외 발생
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create()
    }

    // 호출부
    suspend fun getFoodItems(): FoodResponse? =
        foodApiService.getFoodItems().body()

    // 호출부
    suspend fun getFoodByName(keyword: String): FoodResponse? =
        foodApiService.getFoodByName(keyword).body()

    // 로깅 체크
    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS) // 요청을 시작한 후 서버와의 TCP handshake가 완료되기까지 지속되는 시간이다.
            .readTimeout(30, TimeUnit.SECONDS) // 읽기 시간 초과는 연결이 설정되면 모든 바이트가 전송되는 속도를 감시한다.
            .writeTimeout(30, TimeUnit.SECONDS) // 얼마나 빨리 서버에 바이트를 보낼 수 있는지 확인한다..
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            ).build()


}

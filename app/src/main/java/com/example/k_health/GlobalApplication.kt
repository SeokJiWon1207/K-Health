package com.example.k_health

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

// Kakao SDK 초기화 클래스
class GlobalApplication : Application() {

    companion object {
        lateinit var instance: GlobalApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
        instance = this
    }
}
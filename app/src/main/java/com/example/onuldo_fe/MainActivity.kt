package com.example.onuldo_fe

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.onuldo_fe.navigation.OnuldoApp
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // 시스템 스플래시 설치 → 종료 후 postSplashScreenTheme(Theme.OnulDo_FE)로 전환.
        // 미호출 시 스플래시 테마(Theme.SplashScreen)에 머물러 회색 상단바가 남는다.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 현재 FCM 토큰 로그 — Logcat에서 "FCM" 태그로 확인 (테스트 발송용으로 전달)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "token = ${task.result}")
                } else {
                    Log.w("FCM", "token fetch failed", task.exception)
                }
            }

        setContent {
            OnulDo_FETheme {
                OnuldoApp()
            }
        }
    }
}

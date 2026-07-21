package com.example.onuldo_fe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.onuldo_fe.navigation.OnuldoApp
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

/** 시스템 스플래시 최소 노출 시간(ms) — 브랜드가 잠깐 보이도록 유지. */
private const val SPLASH_HOLD_MS = 800L

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // 시스템 스플래시 설치(주황 배경 + 워드마크). super.onCreate 이전 호출.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // 브랜드가 잠깐 보이도록 스플래시를 짧게 유지 후 랜딩으로 진입(별도 스플래시 화면 없음).
        val shownAt = System.currentTimeMillis()
        splashScreen.setKeepOnScreenCondition {
            System.currentTimeMillis() - shownAt < SPLASH_HOLD_MS
        }
        enableEdgeToEdge()
        setContent {
            OnulDo_FETheme {
                OnuldoApp()
            }
        }
    }
}

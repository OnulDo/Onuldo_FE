package com.example.onuldo_fe

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.onuldo_fe.navigation.OnuldoApp
import com.example.onuldo_fe.navigation.Routes
import com.example.onuldo_fe.repository.auth.AuthRepositoryProvider
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    /**
     * 세션 판정이 끝나기 전까지 null. 이 값이 정해질 때까지 스플래시를 붙잡아 두고,
     * 정해지면 그 화면으로 바로 진입한다. (랜딩을 먼저 그렸다가 홈으로 바꾸면 화면이 깜빡인다.)
     */
    private var startDestination by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {

        // 시스템 스플래시 설치 → 종료 후 postSplashScreenTheme(Theme.OnulDo_FE)로 전환.
        // 미호출 시 스플래시 테마(Theme.SplashScreen)에 머물러 회색 상단바가 남는다.
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { startDestination == null }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 저장된 토큰으로 자동 로그인을 시도한다. 결과에 따라 홈 또는 랜딩으로 진입한다.
        lifecycleScope.launch {
            val restored = AuthRepositoryProvider.provide().restoreSession()
            startDestination = if (restored) Routes.MAIN else Routes.LANDING
        }

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
                // 판정 전에는 아무것도 그리지 않는다 — 위 스플래시가 화면을 덮고 있다.
                startDestination?.let { destination ->
                    OnuldoApp(startDestination = destination)
                }
            }
        }
    }
}

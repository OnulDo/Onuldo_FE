package com.example.onuldo_fe

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.onuldo_fe.navigation.OnuldoApp
import com.example.onuldo_fe.navigation.Routes
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.viewmodel.StartupViewModel
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    /**
     * 자동 로그인 판정. 구성 변경(회전·다크모드)에도 결과가 유지되도록 ViewModel에 둔다.
     * Activity 필드에 두면 재생성마다 재발급 요청이 다시 나가고 `NavHost`가 초기화된다.
     */
    private val startupViewModel: StartupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        // 시스템 스플래시 설치 → 종료 후 postSplashScreenTheme(Theme.OnulDo_FE)로 전환.
        // 미호출 시 스플래시 테마(Theme.SplashScreen)에 머물러 회색 상단바가 남는다.
        val splashScreen = installSplashScreen()

        // 판정이 끝날 때까지 스플래시를 유지한다. 재발급 호출 자체에 상한이 걸려 있어
        // (NetworkModule의 refreshClient callTimeout) 여기서 무한정 대기하지는 않는다.
        splashScreen.setKeepOnScreenCondition { startupViewModel.isSessionRestored == null }

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
                // 판정 전에는 아무것도 그리지 않는다 — 위 스플래시가 화면을 덮고 있다.
                startupViewModel.isSessionRestored?.let { restored ->
                    OnuldoApp(
                        startDestination = if (restored) Routes.MAIN else Routes.LANDING,
                    )
                }
            }
        }
    }
}

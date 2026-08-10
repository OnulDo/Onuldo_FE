package com.example.onuldo_fe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.onuldo_fe.navigation.OnuldoApp
import com.example.onuldo_fe.data.auth.DeviceInfoProvider
import com.example.onuldo_fe.model.home.notification.NotificationLandingBus
import com.example.onuldo_fe.model.home.notification.pushLandingOf
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

        // 콜드 스타트: 백그라운드 상태에서 푸시를 탭해 앱이 새로 뜬 경우, 그 인텐트의
        // data extras로 랜딩 목적지를 결정해 버스에 올린다. (실제 이동은 로그인 상태의
        // MainScreen이 소비 시점에 수행한다.) - 클로드
        handleNotificationIntent(intent)

        // 현재 FCM 토큰 로그 — Logcat에서 "FCM" 태그로 확인 (테스트 발송용으로 전달)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    DeviceInfoProvider.get().saveFcmToken(task.result)
                    Log.d("FCM", "token fetched")
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

    // 앱이 이미 떠 있는(포그라운드·백그라운드) 상태에서 푸시를 탭한 경우.
    // launchMode=singleTop이라 기존 인스턴스로 전달되며, 여기서 새 인텐트를 처리한다.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    /**
     * FCM 알림 메시지를 탭하면 payload의 `data`가 런처 액티비티 인텐트 extras(문자열)로 전달된다.
     * `notificationType`과 `challengeId`/`partyId`를 읽어 랜딩 목적지를 결정해 버스에 올린다.
     */
    private fun handleNotificationIntent(intent: Intent?) {
        val extras = intent?.extras ?: return
        val type = extras.getString("notificationType") ?: extras.getString("type") ?: return
        val challengeId = extras.getString("challengeId")?.toLongOrNull()
        val partyId = extras.getString("partyId")?.toLongOrNull()
        NotificationLandingBus.post(pushLandingOf(type, challengeId, partyId))
        Log.d("FCM", "push tapped: type=$type challengeId=$challengeId partyId=$partyId")
    }
}

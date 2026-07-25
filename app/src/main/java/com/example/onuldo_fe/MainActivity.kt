package com.example.onuldo_fe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.onuldo_fe.navigation.OnuldoApp
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()          // 스플래시 테마 미호출(없으면 상단바 생김)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnulDo_FETheme {
                OnuldoApp()
            }
        }
    }
}

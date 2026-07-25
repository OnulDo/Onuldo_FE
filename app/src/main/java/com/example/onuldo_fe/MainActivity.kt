package com.example.onuldo_fe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.onuldo_fe.navigation.OnuldoApp
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OnulDo_FETheme {
                OnuldoApp()
            }
        }
    }
}

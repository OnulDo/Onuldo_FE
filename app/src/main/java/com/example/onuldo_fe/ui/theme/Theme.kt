package com.example.onuldo_fe.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

private val LightColorScheme = lightColorScheme(
    primary = Persimmon,
    secondary = DarkBrown,
    background = SourCream,

    onPrimary = White,
    onSecondary = White,
    onBackground = BlackBrown
)


@Composable
fun OnulDo_FETheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                content()
            }
        }
    }
}
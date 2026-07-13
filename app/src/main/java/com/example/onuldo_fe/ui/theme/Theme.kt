package com.example.onuldo_fe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
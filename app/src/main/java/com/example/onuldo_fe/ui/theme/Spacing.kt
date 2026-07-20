package com.example.onuldo_fe.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

data class Spacing(
    val spacing8: androidx.compose.ui.unit.Dp = 8.dp,
    val spacing10: androidx.compose.ui.unit.Dp = 10.dp,
    val spacing12: androidx.compose.ui.unit.Dp = 12.dp,
    val spacing16: androidx.compose.ui.unit.Dp = 16.dp,
    val spacing18: androidx.compose.ui.unit.Dp = 18.dp,
    val spacing20: androidx.compose.ui.unit.Dp = 20.dp,
    val spacing24: androidx.compose.ui.unit.Dp = 24.dp,
    val spacing26: androidx.compose.ui.unit.Dp = 26.dp,
    val spacing28: androidx.compose.ui.unit.Dp = 28.dp,
    val spacing30: androidx.compose.ui.unit.Dp = 30.dp,
    val spacing36: androidx.compose.ui.unit.Dp = 36.dp,
    val spacing50: androidx.compose.ui.unit.Dp = 50.dp,
    )

val LocalSpacing = staticCompositionLocalOf { Spacing() }
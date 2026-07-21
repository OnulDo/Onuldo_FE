package com.example.onuldo_fe.ui.screen.home.model

data class TodayChallenge(
    val date: String,
    val progress: Float,
    val completedCount: Int = 0,
    val totalCount: Int = 0
)

package com.example.onuldo_fe.ui.screen.home.data.dto

data class HomeCompletedChallengeDto(
    val type: String,
    val time: String,
    val title: String,
    val completedMemberCount: Int? = null,
    val totalMemberCount: Int? = null,
    val streakDays: Int? = null
)

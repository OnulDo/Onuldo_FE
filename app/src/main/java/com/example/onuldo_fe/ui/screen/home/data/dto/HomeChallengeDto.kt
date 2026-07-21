package com.example.onuldo_fe.ui.screen.home.data.dto

data class HomeChallengeDto(
    val title: String,
    val streakDays: Int,
    val remainingDays: Int,
    val deadlineAt: String,
    val status: String,
    val verifiedAt: String? = null,
    val remainingMinutes: Int? = null,
    val canVerify: Boolean = true
)

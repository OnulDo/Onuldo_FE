package com.example.onuldo_fe.ui.screen.home.data.dto

data class HomePartyChallengeDto(
    val title: String,
    val subtitle: String,
    val remainingDays: Int,
    val deadlineAt: String,
    val completedMemberCount: Int,
    val totalMemberCount: Int,
    val status: String = "NEED_CERTIFICATION",
    val verifiedAt: String? = null,
    val remainingMinutes: Int? = null,
    val canVerify: Boolean = true
)

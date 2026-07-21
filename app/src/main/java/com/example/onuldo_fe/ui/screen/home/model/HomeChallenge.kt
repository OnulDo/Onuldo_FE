package com.example.onuldo_fe.ui.screen.home.model

import java.time.LocalTime

data class HomeChallenge(
    val title: String,
    val streakDays: Int,
    val remainingDays: Int,
    val deadlineAt: LocalTime,
    val status: ChallengeStatus,
    val verifiedAt: LocalTime? = null,
    val remainingMinutes: Int? = null,
    val canVerify: Boolean = true
)

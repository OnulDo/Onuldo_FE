package com.example.onuldo_fe.ui.screen.home.model

import java.time.LocalTime

data class HomePartyChallenge(
    val title: String,
    val subtitle: String,
    val remainingDays: Int,
    val deadlineAt: LocalTime,
    val completedMemberCount: Int,
    val totalMemberCount: Int,
    val status: ChallengeStatus = ChallengeStatus.NeedCertification,
    val verifiedAt: LocalTime? = null,
    val remainingMinutes: Int? = null,
    val canVerify: Boolean = true
)

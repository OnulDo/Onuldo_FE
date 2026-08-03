package com.example.onuldo_fe.data.record.dto

data class OngoingChallengeDto(
    val participationId: Long,
    val challengeId: Long,
    val challengeTitle: String,
    val isVerifiedToday: Boolean,
    val daysUntilEnd: Int,
    val achievementRate: Int,
    val depositAmount: Int,
    val type: String
)
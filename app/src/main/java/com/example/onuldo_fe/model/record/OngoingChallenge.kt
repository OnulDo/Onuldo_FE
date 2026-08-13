package com.example.onuldo_fe.model.record

enum class ChallengeRecordType { PERSONAL, PARTY, UNKNOWN }

enum class OngoingDailyStatus { NEED_CERTIFICATION, REVIEW_PENDING, SUCCESS, FAILURE }

data class OngoingChallenge(
    val participationId: Long,
    val challengeId: Long,
    val title: String,
    val isVerifiedToday: Boolean,
    val daysUntilEnd: Int,
    val achievementRate: Int,
    val depositAmount: Int,
    val type: ChallengeRecordType,
    val dailyStatus: OngoingDailyStatus
)

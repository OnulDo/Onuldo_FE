package com.example.onuldo_fe.data.record.dto

data class CompletedRecordResponseDto(
    val totalCompletedCount: Int?,
    val successRate: Int?,
    val totalSavedAmount: Int?,
    val completedChallenges: List<CompletedChallengeDto>?
)

data class CompletedChallengeDto(
    val participationId: Long?,
    val challengeId: Long?,
    val challengeTitle: String?,
    val resultStatus: String?,
    val depositAmount: Int?,
    val adjustmentAmount: Int?,
    val endDate: String?,
    val achievementRate: Int?
)

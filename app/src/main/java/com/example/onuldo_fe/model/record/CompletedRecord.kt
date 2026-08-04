package com.example.onuldo_fe.model.record

enum class CompletedResultStatus { SUCCESS, FAILURE }

data class CompletedChallenge(
    val participationId: Long,
    val challengeId: Long,
    val title: String,
    val resultStatus: CompletedResultStatus,
    val netAmount: Int,
    val endedDate: String,
    val achievementRate: Int
)

data class CompletedRecordSummary(
    val totalCompletedCount: Int,
    val successRate: Int,
    val totalSavedAmount: Int,
    val challenges: List<CompletedChallenge>
)
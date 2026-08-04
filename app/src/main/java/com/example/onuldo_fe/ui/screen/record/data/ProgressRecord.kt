package com.example.onuldo_fe.ui.screen.record.data

data class ProgressRecord(
    val participationId: Long,
    val challengeId: Long,
    val category: String,
    val title: String,
    val dDay: Int,
    val progress: Int,
    val depositAmount: Int,
    val isTodayVerified: Boolean
)
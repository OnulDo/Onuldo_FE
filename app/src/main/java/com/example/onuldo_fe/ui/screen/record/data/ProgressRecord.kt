package com.example.onuldo_fe.ui.screen.record.data

data class ProgressRecord(
    val participationId: Long = 0,
    val challengeId: Long = 0,
    val category: String,
    val title: String,
    val dDay: Int,
    val progress: Int,
    val depositAmount: Int,
    val isTodayVerified: Boolean
)
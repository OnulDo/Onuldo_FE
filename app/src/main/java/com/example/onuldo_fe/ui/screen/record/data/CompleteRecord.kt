package com.example.onuldo_fe.ui.screen.record.data

data class CompleteRecord(
    val participationId: Long,
    val challengeId: Long,
    val isSuccess: Boolean,
    val title: String,
    val progress: Int,
    val completeDate: String,
    val depositAmount: Int,
    val point: Int
)

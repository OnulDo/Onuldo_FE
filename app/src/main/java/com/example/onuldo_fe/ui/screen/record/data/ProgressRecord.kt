package com.example.onuldo_fe.ui.screen.record.data

data class ProgressRecord(
    val category: String,
    val title: String,
    val dDay: Int,
    val progress: Int,
    val point: Int,
    val isTodayVerified: Boolean
)
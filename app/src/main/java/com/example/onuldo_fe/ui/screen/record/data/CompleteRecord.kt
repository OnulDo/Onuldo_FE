package com.example.onuldo_fe.ui.screen.record.data

data class CompleteRecord(
    val isSuccess: Boolean,
    val title: String,
    val progress: Int,
    val completeDate: String,
    val point: Int
)
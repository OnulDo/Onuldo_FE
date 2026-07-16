package com.example.onuldo_fe.ui.screen.home.model

data class HomePartyChallenge(
    val title: String,
    val subtitle: String,
    val dDay: String,
    val deadline: String,
    val timeLeft: String,
    val completedMemberCount: Int,
    val totalMemberCount: Int
)

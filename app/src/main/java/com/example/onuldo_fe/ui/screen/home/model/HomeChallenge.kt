package com.example.onuldo_fe.ui.screen.home.model

data class HomeChallenge(
    val title: String,
    val subtitle: String,
    val dDay: String,
    val deadline: String,
    val actionText: String,
    val status: ChallengeStatus
)

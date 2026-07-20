package com.example.onuldo_fe.ui.screen.home.model

enum class CompletedChallengeType { Party, Personal }

data class HomeCompletedChallenge(
    val time: String,
    val title: String,
    val resultText: String,
    val type: CompletedChallengeType = CompletedChallengeType.Personal
)

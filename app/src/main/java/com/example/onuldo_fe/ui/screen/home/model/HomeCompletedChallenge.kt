package com.example.onuldo_fe.ui.screen.home.model

sealed interface HomeCompletedChallenge {
    val time: String
    val title: String

    data class Party(
        override val time: String,
        override val title: String,
        val completedMemberCount: Int,
        val totalMemberCount: Int
    ) : HomeCompletedChallenge

    data class Personal(
        override val time: String,
        override val title: String,
        val streakDays: Int
    ) : HomeCompletedChallenge
}

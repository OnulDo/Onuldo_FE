package com.example.onuldo_fe.model.home

sealed interface HomeCompletedChallenge {
    val time: String
    val title: String

    //함께하는 파티
    data class Party(
        override val time: String,
        override val title: String,
        val completedMemberCount: Int,
        val totalMemberCount: Int
    ) : HomeCompletedChallenge

    //나의 챌린지
    data class Personal(
        override val time: String,
        override val title: String,
        val streakDays: Int
    ) : HomeCompletedChallenge
}

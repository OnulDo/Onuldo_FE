package com.example.onuldo_fe.ui.screen.home.data.dto

data class HomeResponseDto(
    val todayChallenge: TodayChallengeDto?,
    val challenges: List<HomeChallengeDto>
)

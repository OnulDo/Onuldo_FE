package com.example.onuldo_fe.ui.screen.home.data.dto

data class HomeResponseDto(
    val userName: String = "",
    val todayChallenge: TodayChallengeDto?,
    val partyChallenges: List<HomePartyChallengeDto>,
    val challenges: List<HomeChallengeDto>,
    val completedChallenges: List<HomeCompletedChallengeDto>,
    val settlementBanner: SettlementBannerDto? = null
)

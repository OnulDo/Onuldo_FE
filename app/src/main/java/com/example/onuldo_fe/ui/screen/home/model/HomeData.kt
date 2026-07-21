package com.example.onuldo_fe.ui.screen.home.model

// 홈 API 한 번의 응답을 앱에서 사용하는 형태로 묶은 모델
data class HomeData(
    val userName: String = "",
    val todayChallenge: TodayChallenge? = null,
    val partyChallenges: List<HomePartyChallenge> = emptyList(),
    val challenges: List<HomeChallenge> = emptyList(),
    val completedChallenges: List<HomeCompletedChallenge> = emptyList(),
    val settlementBanner: SettlementBanner? = null
)

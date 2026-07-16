package com.example.onuldo_fe.ui.screen.home

import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.screen.home.model.HomeCompletedChallenge
import com.example.onuldo_fe.ui.screen.home.model.HomePartyChallenge
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge

data class HomeUiState(
    val todayChallenge: TodayChallenge? = null,
    val partyChallenges: List<HomePartyChallenge> = emptyList(),
    val challenges: List<HomeChallenge> = emptyList(),
    val completedChallenges: List<HomeCompletedChallenge> = emptyList()
) {
    val hasHomeContent: Boolean
        // 홈에 표시할 데이터가 하나라도 있으면 데이터 있는 홈으로 판단
        get() = todayChallenge != null ||
            partyChallenges.isNotEmpty() ||
            challenges.isNotEmpty() ||
            completedChallenges.isNotEmpty()
}

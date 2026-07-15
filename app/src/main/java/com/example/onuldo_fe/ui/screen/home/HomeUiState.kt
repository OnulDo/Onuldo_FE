package com.example.onuldo_fe.ui.screen.home

import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge

data class HomeUiState(
    val todayChallenge: TodayChallenge? = null,
    val challenges: List<HomeChallenge> = emptyList()
) {
    val hasChallenge: Boolean
        // 오늘의 챌린지와 목록이 모두 있을 때 데이터 있는 홈으로 판단
        get() = todayChallenge != null && challenges.isNotEmpty()
}

package com.example.onuldo_fe.ui.screen.home

import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.screen.home.model.HomeCompletedChallenge
import com.example.onuldo_fe.ui.screen.home.model.HomePartyChallenge
import com.example.onuldo_fe.ui.screen.home.model.SettlementBanner
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge

enum class HomeContentMode { Empty, Default, AllCompleted }

data class HomeUiState(
    val userName: String = "",
    val todayChallenge: TodayChallenge? = null,
    val partyChallenges: List<HomePartyChallenge> = emptyList(),
    val challenges: List<HomeChallenge> = emptyList(),
    val completedChallenges: List<HomeCompletedChallenge> = emptyList(),
    val settlementBanner: SettlementBanner? = null
) {
    val contentMode: HomeContentMode
        get() = when {
            todayChallenge?.let { it.totalCount > 0 && it.completedCount == it.totalCount } == true -> HomeContentMode.AllCompleted
            partyChallenges.isEmpty() && challenges.isEmpty() && completedChallenges.isEmpty() -> HomeContentMode.Empty
            else -> HomeContentMode.Default
        }

    val hasHomeContent get() = contentMode != HomeContentMode.Empty
    val isAllCompleted get() = contentMode == HomeContentMode.AllCompleted
}

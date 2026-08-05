package com.example.onuldo_fe.viewmodel.home

import com.example.onuldo_fe.model.home.HomeChallenge
import com.example.onuldo_fe.model.home.HomeCompletedChallenge
import com.example.onuldo_fe.model.home.HomeData
import com.example.onuldo_fe.model.home.HomePartyChallenge
import com.example.onuldo_fe.model.home.SettlementBanner
import com.example.onuldo_fe.model.home.TodayChallenge

enum class HomeContentMode { Empty, Default, AllCompleted }

data class HomeUiState(
    val userName: String = "",
    val userProfileImageUrl: String? = null,
    val todayChallenge: TodayChallenge? = null,
    val partyChallenges: List<HomePartyChallenge> = emptyList(),
    val challenges: List<HomeChallenge> = emptyList(),
    val completedChallenges: List<HomeCompletedChallenge> = emptyList(),
    val settlementBanner: SettlementBanner? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
) {
    // 홈 API 데이터 조합에 따른 화면 상태 결정
    val contentMode: HomeContentMode
        get() = when {
            todayChallenge?.let {
                it.totalCount > 0 && it.completedCount >= it.totalCount
            } == true && completedChallenges.isNotEmpty() -> HomeContentMode.AllCompleted
            todayChallenge == null &&
                partyChallenges.isEmpty() &&
                challenges.isEmpty() &&
                completedChallenges.isEmpty() &&
                settlementBanner == null -> HomeContentMode.Empty
            else -> HomeContentMode.Default
        }

    val hasHomeContent get() = contentMode != HomeContentMode.Empty
    val isAllCompleted get() = contentMode == HomeContentMode.AllCompleted
}

// Repository 모델을 화면용 UI 상태로 변환
internal fun HomeData.toUiState() = HomeUiState(
    userName = userName,
    userProfileImageUrl = userProfileImageUrl,
    todayChallenge = todayChallenge,
    partyChallenges = partyChallenges,
    challenges = challenges,
    completedChallenges = completedChallenges,
    settlementBanner = settlementBanner
)

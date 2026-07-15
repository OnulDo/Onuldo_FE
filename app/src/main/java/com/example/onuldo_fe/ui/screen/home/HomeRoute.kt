package com.example.onuldo_fe.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val todayChallenge = uiState.todayChallenge

    // 데이터 유무에 따른 챌린지 목록 화면/빈 홈 화면 분기
    if (uiState.hasChallenge && todayChallenge != null) {
        HomeChallengeScreen(
            todayChallenge = todayChallenge,
            challenges = uiState.challenges
        )
    } else {
        HomeScreen()
    }
}

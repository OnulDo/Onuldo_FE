package com.example.onuldo_fe.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.onuldo_fe.ui.screen.home.data.repository.HomeRepository
import com.example.onuldo_fe.ui.screen.home.data.repository.HomeRepositoryProvider

class HomeViewModel(
    private val repository: HomeRepository = HomeRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        loadHome()
    }

    fun loadHome() {
        // Repository 데이터를 Compose가 관찰할 수 있는 화면 상태로 저장
        uiState = HomeUiState(
            todayChallenge = repository.getTodayChallenge(),
            partyChallenges = repository.getPartyChallenges(),
            challenges = repository.getChallenges(),
            completedChallenges = repository.getCompletedChallenges()
        )
    }
}

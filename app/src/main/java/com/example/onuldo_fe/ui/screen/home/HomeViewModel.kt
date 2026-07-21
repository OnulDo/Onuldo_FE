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

    init { loadHome() }

    fun loadHome() {
        uiState = HomeUiState(
            userName = repository.getUserName(),
            todayChallenge = repository.getTodayChallenge(),
            partyChallenges = repository.getPartyChallenges(),
            challenges = repository.getChallenges(),
            completedChallenges = repository.getCompletedChallenges(),
            settlementBanner = repository.getSettlementBanner()
        )
    }

    fun markSettlementResultChecked() {
        uiState = uiState.copy(settlementBanner = null)
    }
}

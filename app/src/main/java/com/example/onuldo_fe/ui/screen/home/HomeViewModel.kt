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
        // 홈 응답 전체로 동일 시점의 UI 상태 생성
        uiState = repository.getHome().toUiState()
    }

    fun confirmSettlementResult() {
        // 정산 결과 화면 진입이 확인된 뒤 배너 제거
        if (uiState.settlementBanner == null) return
        uiState = uiState.copy(settlementBanner = null)
    }
}

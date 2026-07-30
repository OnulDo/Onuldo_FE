package com.example.onuldo_fe.viewmodel.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.onuldo_fe.repository.home.HomeRepository
import com.example.onuldo_fe.repository.home.HomeRepositoryProvider

class HomeViewModel(
    private val repository: HomeRepository = HomeRepositoryProvider.provide()
) : ViewModel() {
    private val confirmedSettlementResultIds = mutableSetOf<String>()

    var uiState by mutableStateOf(HomeUiState())
        private set

    init { loadHome() }

    fun loadHome() {
        val loadedState = repository.getHome().toUiState()
        val settlementResultId = loadedState.settlementBanner?.resultId

        // 현재 실행 중 이미 확인한 정산 결과는 홈을 다시 불러와도 숨김
        uiState = if (settlementResultId in confirmedSettlementResultIds) {
            loadedState.copy(settlementBanner = null)
        } else {
            loadedState
        }
    }

    fun confirmSettlementResult() {
        val resultId = uiState.settlementBanner?.resultId ?: return

        // TODO: 실제 API 연동 후 서버의 정산 결과 확인 처리로 교체
        confirmedSettlementResultIds += resultId
        uiState = uiState.copy(settlementBanner = null)
    }
}

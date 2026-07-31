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
    private val confirmedSettlementPartyIds = mutableSetOf<Long>()

    var uiState by mutableStateOf(HomeUiState())
        private set

    init { loadHome() }

    fun loadHome() {
        val loadedState = repository.getHome().toUiState()
        val settlementPartyId = loadedState.settlementBanner?.partyId

        // 현재 실행 중 이미 확인한 정산 결과는 홈을 다시 불러와도 숨김
        uiState = if (settlementPartyId in confirmedSettlementPartyIds) {
            loadedState.copy(settlementBanner = null)
        } else {
            loadedState
        }
    }

    fun confirmSettlementResult() {
        val partyId = uiState.settlementBanner?.partyId ?: return

        // TODO: 실제 API 연동 후 서버의 정산 결과 확인 처리로 교체
        confirmedSettlementPartyIds += partyId
        uiState = uiState.copy(settlementBanner = null)
    }
}

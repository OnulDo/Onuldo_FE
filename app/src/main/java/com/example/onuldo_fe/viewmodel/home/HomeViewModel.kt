package com.example.onuldo_fe.viewmodel.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.repository.home.HomeRepository
import com.example.onuldo_fe.repository.home.HomeRepositoryProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository = HomeRepositoryProvider.provide()
) : ViewModel() {
    private val confirmedSettlementPartyIds = mutableSetOf<Long>()

    var uiState by mutableStateOf(HomeUiState(isLoading = true))
        private set

    init { loadHome() }

    fun loadHome() {
        // API 호출 전 로딩 상태를 표시한다.
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                // Repository가 Fake/Real 차이를 처리하므로 결과만 화면 상태로 변환한다.
                val loadedState = repository.getHome().toUiState()
                val settlementPartyId = loadedState.settlementBanner?.partyId
                uiState = if (settlementPartyId in confirmedSettlementPartyIds) {
                    loadedState.copy(settlementBanner = null)
                } else {
                    loadedState
                }
            } catch (error: CancellationException) {
                // 코루틴 취소는 오류 화면으로 처리하지 않는다.
                throw error
            } catch (error: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "홈 정보를 불러오지 못했어요."
                )
            }
        }
    }

    fun confirmSettlementResult() {
        val partyId = uiState.settlementBanner?.partyId ?: return
        confirmedSettlementPartyIds += partyId
        uiState = uiState.copy(settlementBanner = null)
    }
}

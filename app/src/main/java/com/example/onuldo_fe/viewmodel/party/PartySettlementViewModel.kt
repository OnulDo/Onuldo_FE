package com.example.onuldo_fe.viewmodel.party

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.repository.party.PartyRepository
import com.example.onuldo_fe.repository.party.PartyRepositoryProvider
import kotlinx.coroutines.launch

// partyId로 정산 결과를 조회하고 화면 상태 관리
class PartySettlementViewModel(
    private val repository: PartyRepository = PartyRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(PartySettlementUiState())
        private set

    fun loadSettlementResult(partyId: Long) {
        uiState = PartySettlementUiState(isLoading = true)
        viewModelScope.launch {
            runCatching { repository.getSettlementResult(partyId) }
                .onSuccess { result ->
                    uiState = PartySettlementUiState(result = result)
                }
                .onFailure {
                    uiState = PartySettlementUiState(
                        errorMessage = "파티 정산 결과를 불러오지 못했어요."
                    )
                }
        }
    }
}

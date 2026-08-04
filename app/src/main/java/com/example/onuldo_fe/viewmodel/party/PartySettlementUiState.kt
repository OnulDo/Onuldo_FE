package com.example.onuldo_fe.viewmodel.party

import com.example.onuldo_fe.model.party.PartySettlementResult

// 파티 정산 결과 화면의 로딩·성공·실패 상태
data class PartySettlementUiState(
    val isLoading: Boolean = false,
    val result: PartySettlementResult? = null,
    val errorMessage: String? = null
)

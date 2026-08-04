package com.example.onuldo_fe.ui.screen.party

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.viewmodel.party.PartySettlementViewModel

// 파티 탭과 홈 배너가 같은 정산 결과 조회 화면을 사용하도록 연결
@Composable
fun PartySettlementRoute(
    partyId: Long,
    onBack: () -> Unit,
    viewModel: PartySettlementViewModel = viewModel()
) {
    LaunchedEffect(partyId) {
        viewModel.loadSettlementResult(partyId)
    }

    val state = viewModel.uiState
    val result = state.result
    if (result == null) {
        PartyLoadingScreen(
            errorMessage = state.errorMessage,
            onRetry = { viewModel.loadSettlementResult(partyId) },
            onBack = onBack
        )
    } else {
        PartySettlementScreen(
            result = result,
            onBack = onBack
        )
    }
}

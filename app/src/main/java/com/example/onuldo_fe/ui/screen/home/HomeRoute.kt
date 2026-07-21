package com.example.onuldo_fe.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.viewmodel.home.HomeViewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = viewModel(),
    onNotificationClick: () -> Unit = {},
    onSettlementResultClick: (String) -> Unit = {}
) {
    HomeScreen(
        uiState = viewModel.uiState,
        onNotificationClick = onNotificationClick,
        // 정산 결과 화면 연결 지점에 결과 식별자 전달
        onSettlementResultClick = onSettlementResultClick
    )
}

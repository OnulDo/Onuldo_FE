package com.example.onuldo_fe.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.viewmodel.home.HomeViewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = viewModel(),
    onSettlementResultClick: (String) -> Unit = {}
) {
    // 네비게이션 라이브러리 도입 전 홈 <-> 알림 전환 상태
    var showNotification by rememberSaveable { mutableStateOf(false) }

    // 뒤로가기 제스처로 홈 복귀 (if문)
    if (showNotification) {
        BackHandler { showNotification = false }
        NotificationRoute(onBackClick = { showNotification = false })
    } else {
        HomeScreen(
            uiState = viewModel.uiState,
            // 알림 아이콘 클릭 시 알림 화면으로 전환 추가 TODO: shion
            onNotificationClick = { showNotification = true },
            // 정산 결과 화면 연결 지점에 결과 식별자 전달
            onSettlementResultClick = onSettlementResultClick
        )
    }
}

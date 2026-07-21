package com.example.onuldo_fe.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.screen.home.notification.NotificationDummyData
import com.example.onuldo_fe.ui.screen.home.notification.NotificationScreen

private enum class HomeRouteScreen {
    Home,
    Notification
}

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = viewModel(),
    onSettlementResultClick: (String) -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf(HomeRouteScreen.Home) }

    when (currentScreen) {
        HomeRouteScreen.Home -> HomeScreen(
            uiState = viewModel.uiState,
            onNotificationClick = {
                currentScreen = HomeRouteScreen.Notification
            },
            // 정산 결과 화면 연결 지점에 결과 식별자 전달
            onSettlementResultClick = onSettlementResultClick
        )

        HomeRouteScreen.Notification -> NotificationScreen(
            notifications = NotificationDummyData.items,
            onBackClick = {
                currentScreen = HomeRouteScreen.Home
            }
        )
    }
}

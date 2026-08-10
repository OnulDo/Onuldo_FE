package com.example.onuldo_fe.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.model.home.notification.NotificationItem
import com.example.onuldo_fe.viewmodel.notification.NotificationViewModel

@Composable
fun NotificationRoute(
    viewModel: NotificationViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onItemClick: (NotificationItem) -> Unit = {}
) {
    // 알림함 최신화 + 새로고침 추가
    LaunchedEffect(Unit) { viewModel.loadNotifications() }
    NotificationScreen(
        uiState = viewModel.uiState,
        onBackClick = onBackClick,
        onItemClick = onItemClick,
        onRetry = viewModel::loadNotifications,
        onRefresh = viewModel::loadNotifications,
        onLoadMore = viewModel::loadMore
    )
}

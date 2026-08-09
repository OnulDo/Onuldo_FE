package com.example.onuldo_fe.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.model.home.notification.NotificationItem
import com.example.onuldo_fe.viewmodel.notification.NotificationViewModel

@Composable
fun NotificationRoute(
    viewModel: NotificationViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onItemClick: (NotificationItem) -> Unit = {}
) {
    NotificationScreen(
        uiState = viewModel.uiState,
        onBackClick = onBackClick,
        onItemClick = onItemClick,
        onRetry = viewModel::loadNotifications,
        onLoadMore = viewModel::loadMore
    )
}

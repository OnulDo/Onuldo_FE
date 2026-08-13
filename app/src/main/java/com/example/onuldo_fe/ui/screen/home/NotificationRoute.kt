package com.example.onuldo_fe.ui.screen.home

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.model.home.notification.NotificationItem
import com.example.onuldo_fe.viewmodel.notification.NotificationViewModel

@Composable
fun NotificationRoute(
    viewModel: NotificationViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onItemClick: (NotificationItem) -> Unit = {}
) {
    val context = LocalContext.current

    // 알림함 최신화 + 새로고침 추가
    LaunchedEffect(Unit) { viewModel.loadNotifications() }

    // 다음 페이지(무한 스크롤) 조회 실패는 재시도 UI 없이 Toast로만 안내한다.
    val loadMoreError = viewModel.uiState.loadMoreErrorMessage
    LaunchedEffect(loadMoreError) {
        if (loadMoreError != null) {
            Toast.makeText(context, loadMoreError, Toast.LENGTH_SHORT).show()
            viewModel.onLoadMoreErrorShown()
        }
    }

    NotificationScreen(
        uiState = viewModel.uiState,
        onBackClick = onBackClick,
        onItemClick = onItemClick,
        onRetry = viewModel::loadNotifications,
        onRefresh = viewModel::loadNotifications,
        onLoadMore = viewModel::loadMore
    )
}

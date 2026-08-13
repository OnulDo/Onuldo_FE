package com.example.onuldo_fe.viewmodel.notification

import com.example.onuldo_fe.model.home.notification.NotificationItem

data class NotificationUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false,      // 첫 페이지 로딩
    val isLoadingMore: Boolean = false,  // 다음 페이지(무한 스크롤) 로딩
    val hasNext: Boolean = false,
    val errorMessage: String? = null,
    // 다음 페이지 조회 실패 문구. errorMessage와 분리해, 목록이 이미 있을 때(당겨서 새로고침 실패 등)
    // 그 에러가 무한 스크롤 재시도 UI에 잘못 섞여 보이지 않게 한다.
    val loadMoreErrorMessage: String? = null,
) {
    // 로딩이 끝났는데 목록이 비어 있을 때만 빈 화면을 노출한다.
    val isEmpty get() = notifications.isEmpty() && !isLoading
}

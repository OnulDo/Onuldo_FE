package com.example.onuldo_fe.viewmodel.notification

import com.example.onuldo_fe.model.home.notification.NotificationData
import com.example.onuldo_fe.model.home.notification.NotificationItem

data class NotificationUiState(
    val notifications: List<NotificationItem> = emptyList()
) {
    // 알림이 하나도 없을 때 빈 화면 노출
    val isEmpty get() = notifications.isEmpty()
}

// Repository 모델을 화면용 UI 상태로 변환
internal fun NotificationData.toUiState() = NotificationUiState(
    notifications = notifications
)

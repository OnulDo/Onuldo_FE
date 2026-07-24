package com.example.onuldo_fe.model.home.notification

// 알림 API 한 번의 응답을 앱에서 사용하는 형태로 묶은 모델
data class NotificationData(
    val notifications: List<NotificationItem> = emptyList()
)

package com.example.onuldo_fe.model.home.notification

// 알림 한 건을 앱에서 사용하는 형태로 묶은 모델
data class NotificationItem(
    val title: String,   // 알림 제목
    val content: String, // 알림 내용
    val time: String,    // 표시용 시간 ("방금", "어제", "3일 전" 등)
    val type: NotificationType
)

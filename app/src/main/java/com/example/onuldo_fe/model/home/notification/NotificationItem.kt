package com.example.onuldo_fe.model.home.notification

import java.time.LocalDateTime

// 알림 한 건을 앱에서 사용하는 형태로 묶은 모델 TODO: API에 따라 변동 가능
data class NotificationItem(
    val title: String,            // 알림 제목
    val content: String,          // 알림 내용
    val createdAt: LocalDateTime, // 실제 발생 시각 (API 값을 이 타입으로 받음)
    val type: NotificationType
) {
    // 표시용 상대 시각 ("방금", "N시간 전", "어제", "N일 전")
    val timeLabel: String get() = RelativeTime.from(createdAt).label
}

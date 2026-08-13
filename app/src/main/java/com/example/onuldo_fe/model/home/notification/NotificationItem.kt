package com.example.onuldo_fe.model.home.notification

// 알림 한 건을 앱에서 사용하는 형태로 묶은 모델.
// 상대 시간은 서버가 만든 timeAgo 문구를 그대로 쓴다(클라 계산 없음).
data class NotificationItem(
    val notificationId: Long,
    val title: String,            // 알림 제목
    val content: String,          // 알림 내용
    val timeAgo: String,          // 서버가 만든 상대 시간 문구 ("3분 전" 등)
    val createdAt: String,        // ISO-8601 date-time (정렬/향후용)
    val type: NotificationType,
    val challengeId: Long? = null, // 탭 시 챌린지로 이동
    val partyId: Long? = null,     // 탭 시 파티로 이동
) {
    // 화면 호환용 별칭 — 서버 timeAgo를 그대로 노출한다.
    val timeLabel: String get() = timeAgo
}

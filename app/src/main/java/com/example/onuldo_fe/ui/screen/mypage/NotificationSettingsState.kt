package com.example.onuldo_fe.ui.screen.mypage

// 알림 설정 화면 상태 (더미 기본값 — API 연동 시 서버 값으로 교체)
// TODO(알림 스펙 변경 예정): [NotificationType]이 "8유형"으로 바뀌면 이 필드들도 그에 맞춰 교체.
data class NotificationSettingsState(
    val all: Boolean = true,
    val challengeStart: Boolean = true,
    val deadline: Boolean = true,
    val endReminder: Boolean = true,
    val result: Boolean = true,
    // 파티원 인증 완료 — 서버 PATCH 타입이 없어 로컬 전용 (인증 결과와 상태 공유 금지)
    val partyMemberVerified: Boolean = true,
    val refund: Boolean = true,
    val deduction: Boolean = false
)
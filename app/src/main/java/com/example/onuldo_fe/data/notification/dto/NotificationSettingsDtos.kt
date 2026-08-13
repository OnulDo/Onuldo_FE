package com.example.onuldo_fe.data.notification.dto

/**
 * 알림 설정 항목. 서버 `UpdateNotificationReqDto.type` enum과 이름이 일치해야 한다.
 *
 * [ALL_ENABLED]는 마스터 스위치 전용 타입 — true면 개별 6종을 전부 켜고, false면
 * [SETTLEMENT_COMPLETE]를 제외한 나머지를 끈다(정산/환급은 필수 알림 정책이라 서버가 유지한다).
 */
enum class NotificationSettingType {
    ALL_ENABLED,
    VERIFICATION_DEADLINE,
    CHALLENGE_START,
    CHALLENGE_END_REMINDER,
    VERIFICATION_RESULT,
    PARTY_MEMBER_VERIFIED,
    SETTLEMENT_COMPLETE,
}

/** `GET /api/users/me/notification-settings` */
data class NotificationSettingsResponseDto(
    val allEnabled: Boolean = false,
    val verificationDeadline: Boolean = false,
    val challengeStart: Boolean = false,
    val challengeEndReminder: Boolean = false,
    val verificationResult: Boolean = false,
    val partyMemberVerified: Boolean = false,
    val settlementComplete: Boolean = false,
)

/** `PATCH /api/users/me/notification-settings` — 항목 하나씩 토글한다. */
data class UpdateNotificationRequestDto(
    val type: NotificationSettingType,
    val enabled: Boolean,
)

data class UpdateNotificationResponseDto(
    val type: NotificationSettingType? = null,
    val enabled: Boolean = false,
)

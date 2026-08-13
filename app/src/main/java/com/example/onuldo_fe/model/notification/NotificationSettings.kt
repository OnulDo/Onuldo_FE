package com.example.onuldo_fe.model.notification

/**
 * 알림 설정 상태.
 *
 * [allEnabled]는 개별 항목과 독립적인 마스터 스위치다. `PATCH /notification-settings`에
 * `type=ALL_ENABLED`로 저장하며, true면 개별 6종 전부, false면 [settlementComplete]를
 * 제외한 나머지가 서버에서 꺼진다(정산/환급은 필수 알림 정책이라 항상 유지).
 */
data class NotificationSettings(
    val allEnabled: Boolean,
    val verificationDeadline: Boolean,
    val challengeStart: Boolean,
    val challengeEndReminder: Boolean,
    val verificationResult: Boolean,
    val partyMemberVerified: Boolean,
    val settlementComplete: Boolean,
) {
    companion object {
        /** 서버 응답 전 임시 기본값. 서버 응답이 정상적으로 반영되는지 확인하기 위해 모두 false로 설정 */
        val EMPTY = NotificationSettings(
            allEnabled = false,
            verificationDeadline = false,
            challengeStart = false,
            challengeEndReminder = false,
            verificationResult = false,
            partyMemberVerified = false,
            // 정산·환급은 정책상 항상 발송이라 로딩 전 기본도 켜짐(true).
            settlementComplete = true,
        )
    }
}

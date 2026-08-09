package com.example.onuldo_fe.model.notification

/**
 * 알림 설정 상태.
 *
 * [allEnabled]는 개별 항목과 독립적인 마스터 스위치다. 다만 `PATCH /notification-settings`의
 * type enum에 `ALL`이 없어 마스터 자체는 서버에 저장되지 않으므로(조회 응답만 담음), 화면은
 * 개별 항목에서 마스터 상태를 파생한다(`NotificationSettingsViewModel`의 변환 참고).
 * 서버에 `ALL` 타입이 추가되면 이 값을 그대로 쓰도록 되돌릴 수 있다.
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

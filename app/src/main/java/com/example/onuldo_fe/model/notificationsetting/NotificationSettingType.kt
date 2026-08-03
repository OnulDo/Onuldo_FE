package com.example.onuldo_fe.model.notificationsetting

// PATCH /api/users/me/notification-settings 의 알림 타입 (개별 토글 on/off)
// 주의: 마스터 토글(allEnabled)은 PATCH 타입 목록에 없음 → 저장 방식 백엔드 확인 필요
enum class NotificationSettingType(val apiValue: String) {
    ChallengeStart("CHALLENGE_START"),        // 챌린지 시작 알림
    VerificationDeadline("VERIFICATION_DEADLINE"), // 인증 마감 임박 알림
    VerificationResult("VERIFICATION_RESULT"),     // 인증 결과 알림
    RefundComplete("REFUND_COMPLETE"),        // 환급 완료 알림
    DeductionAlert("DEDUCTION_ALERT")         // 차감 알림
}

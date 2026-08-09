package com.example.onuldo_fe.model.home.notification

// 표시 유형 — 알림함 아이콘 매핑의 기준. //TODO: 변동 가능
// 정책서(NOTI-02) 카테고리는 8유형이나, "인증 마감 리마인더"의 경고성 변형(NOTI-03)과
// "파티 일별 정산"(알림함 전용, NOTI-03)이 별도 아이콘을 써서 표시 기준으로는 10종.
enum class NotificationType {
    DeadlineReminder,     // 인증 마감 리마인더 (일반, 시계)
    DeadlineWarning,      // 인증 마감 리마인더 - 경고성 (마감 1시간 전·22:00, 느낌표) NOTI-03
    ReviewPassed,         // 직접검토 결과 - 통과
    ReviewRejected,       // 직접검토 결과 - 기각 (사유·차감 포인트 포함)
    PartyMemberVerified,  // 파티원 인증 완료 (파티원 닉네임 포함)
    ChallengeStart,       // 새 챌린지 시작
    ChallengeEndReminder, // 챌린지 종료일 리마인더 (5/3/1일 전)
    SoloRefund,           // 솔로 환급 완료 (지급액)
    PartySettlement,      // 파티 정산 완료 (지급액)
    PartyDailySettlement  // 파티 일별 정산 (알림함 전용, '종료 시 지급') NOTI-03
}

/**
 * 서버 알림 목록의 `type` 문자열 → 표시 유형.
 *
 * 서버 7종: VERIFICATION_DEADLINE / VERIFICATION_RESULT / PARTY_MEMBER_VERIFIED /
 * CHALLENGE_START / CHALLENGE_END_REMINDER / REFUND_COMPLETE / PARTY_SETTLEMENT_COMPLETE.
 * 알 수 없는 값은 마감 리마인더 아이콘으로 안전 처리한다.
 * TODO: 아이콘 세분화(경고성/통과·기각 구분 등)는 추후.
 */
fun notificationTypeFrom(raw: String?): NotificationType = when (raw) {
    "VERIFICATION_DEADLINE" -> NotificationType.DeadlineReminder
    "VERIFICATION_RESULT" -> NotificationType.ReviewPassed
    "PARTY_MEMBER_VERIFIED" -> NotificationType.PartyMemberVerified
    "CHALLENGE_START" -> NotificationType.ChallengeStart
    "CHALLENGE_END_REMINDER" -> NotificationType.ChallengeEndReminder
    "REFUND_COMPLETE" -> NotificationType.SoloRefund
    "PARTY_SETTLEMENT_COMPLETE" -> NotificationType.PartySettlement
    else -> NotificationType.DeadlineReminder
}

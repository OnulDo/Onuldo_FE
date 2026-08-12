package com.example.onuldo_fe.model.home.notification

/**
 * 각 호스트는 이 목적지를 실제 네비게이션으로 실행하되, 아직 없는 화면은 [Home]으로 폴백한다.
 */
sealed interface NotificationLanding {
    data object Home : NotificationLanding
    data class ChallengeDetail(val challengeId: Long) : NotificationLanding
    data class PartySettlement(val partyId: Long) : NotificationLanding
    data class PartyFeed(val partyId: Long) : NotificationLanding
    data object SoloRecord : NotificationLanding

    // 기기 푸시 탭 랜딩
    data object RecordOngoing : NotificationLanding   // 기록 진행중 탭
    data object RecordCompleted : NotificationLanding // 기록 완료 탭
}

/**
 * 기기 푸시(FCM) 탭 → 랜딩 목적지 결정
 * - VERIFICATION_DEADLINE / CHALLENGE_START → 홈
 * - VERIFICATION_APPROVED / VERIFICATION_REJECTED
 *   → challengeId 있으면 챌린지 상세, 없으면 홈
 * - CHALLENGE_END_REMINDER → 기록 진행중 탭
 * - REFUND_COMPLETE → 기록 완료 탭
 * - PARTY_MEMBER_VERIFIED → partyId 있으면 파티 피드, 없으면 홈
 * - PARTY_SETTLEMENT_COMPLETE → partyId 있으면 파티 정산 결과, 없으면 홈
 * - 예외)))) 그 외/미상 → 홈(안전값)
 */
fun pushLandingOf(
    notificationType: String?,
    challengeId: Long?,
    partyId: Long?,
): NotificationLanding = when (notificationType) {
    "VERIFICATION_DEADLINE", "CHALLENGE_START" -> NotificationLanding.Home
    // 승인/기각 모두 해당 챌린지 상세로.
    "VERIFICATION_APPROVED", "VERIFICATION_REJECTED" ->
        challengeId?.let(NotificationLanding::ChallengeDetail) ?: NotificationLanding.Home
    "CHALLENGE_END_REMINDER" -> NotificationLanding.RecordOngoing
    "REFUND_COMPLETE" -> NotificationLanding.RecordCompleted
    "PARTY_MEMBER_VERIFIED" ->
        partyId?.let(NotificationLanding::PartyFeed) ?: NotificationLanding.Home
    "PARTY_SETTLEMENT_COMPLETE" ->
        partyId?.let(NotificationLanding::PartySettlement) ?: NotificationLanding.Home
    else -> NotificationLanding.Home
}

/**
 * 알림함 목록 클릭 → 이동 목적지 결정. **[pushLandingOf]와 같은 규칙**을 쓴다(리스트/푸시 통일).
 *
 * - 마감 리마인더/챌린지 시작 → 홈
 * - 종료일 리마인더 → 기록 진행중 탭
 * - 인증 승인/기각 → challengeId 있으면 챌린지 상세, 없으면 홈
 * - 개인 환급 완료 → 기록 완료 탭
 * - 파티 정산/일별 정산 → partyId 있으면 파티 정산 결과, 없으면 홈
 * - 파티원 인증 완료 → partyId 있으면 파티 피드, 없으면 홈
 */
fun NotificationItem.toLanding(): NotificationLanding = when (type) {
    NotificationType.DeadlineReminder,
    NotificationType.DeadlineWarning,
    NotificationType.ChallengeStart -> NotificationLanding.Home

    NotificationType.ChallengeEndReminder -> NotificationLanding.RecordOngoing

    NotificationType.ReviewPassed,
    NotificationType.ReviewRejected ->
        challengeId?.let(NotificationLanding::ChallengeDetail) ?: NotificationLanding.Home

    NotificationType.SoloRefund -> NotificationLanding.RecordCompleted

    NotificationType.PartySettlement,
    NotificationType.PartyDailySettlement ->
        partyId?.let(NotificationLanding::PartySettlement) ?: NotificationLanding.Home

    NotificationType.PartyMemberVerified ->
        partyId?.let(NotificationLanding::PartyFeed) ?: NotificationLanding.Home
}

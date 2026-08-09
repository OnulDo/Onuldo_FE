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
}

/**
 * 알림 한 건 → 이동 목적지 결정.
 *
 * - 마감 리마인더/챌린지 시작·종료 리마인더 → 홈
 * - 인증 결과 → challengeId 있으면 챌린지 상세, 없으면 홈
 * - 환급 완료 → partyId 있으면 파티 정산 결과, 없으면 솔로 기록 완료
 * - 파티 정산/일별 정산 → partyId 있으면 파티 정산 결과, 없으면 홈
 * - 파티원 인증 완료 → partyId 있으면 파티 피드, 없으면 홈
 */
fun NotificationItem.toLanding(): NotificationLanding = when (type) {
    NotificationType.DeadlineReminder,
    NotificationType.DeadlineWarning,
    NotificationType.ChallengeStart,
    NotificationType.ChallengeEndReminder -> NotificationLanding.Home

    NotificationType.ReviewPassed,
    NotificationType.ReviewRejected ->
        challengeId?.let(NotificationLanding::ChallengeDetail) ?: NotificationLanding.Home

    NotificationType.SoloRefund ->
        if (partyId != null) NotificationLanding.PartySettlement(partyId) else NotificationLanding.SoloRecord

    NotificationType.PartySettlement,
    NotificationType.PartyDailySettlement ->
        partyId?.let(NotificationLanding::PartySettlement) ?: NotificationLanding.Home

    NotificationType.PartyMemberVerified ->
        partyId?.let(NotificationLanding::PartyFeed) ?: NotificationLanding.Home
}

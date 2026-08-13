package com.example.onuldo_fe.ui.screen.mypage

// 알림 설정 화면 상태. 서버 6종 개별 항목 + 마스터(all)와 매핑된다.
// (NotificationSettingsViewModel의 toUiState/apply 참고)
data class NotificationSettingsState(
    val all: Boolean = true,
    val challengeStart: Boolean = true,        // CHALLENGE_START
    val deadline: Boolean = true,              // VERIFICATION_DEADLINE
    val endReminder: Boolean = true,           // CHALLENGE_END_REMINDER (종료일 리마인더)
    val result: Boolean = true,                // VERIFICATION_RESULT
    val partyMemberVerified: Boolean = true,   // PARTY_MEMBER_VERIFIED (파티원 인증 완료)
    val refund: Boolean = true,                // SETTLEMENT_COMPLETE (정산/환급 완료) / 항상 true
)
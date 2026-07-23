package com.example.onuldo_fe.model.home.notification

// 알림 종류 — 현재 UI 아이콘 매핑의 기준 TODO: 기능명세서 참고 후 변경
enum class NotificationType {
    Deadline,            // 인증 마감 임박
    VerificationSuccess, // 인증 완료
    ChallengeStart,      // 새 챌린지 시작
    Refund,              // 환급 완료
    VerificationFail     // 인증 실패 차감
}

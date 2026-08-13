package com.example.onuldo_fe.model.home

// 홈 챌린지 카드에 표시할 오늘의 인증 상태
enum class ChallengeStatus {
    NeedCertification,                        // 인증이 필요하고 인증 가능한 상태
    WaitingReview,                            // 인증 제출 후 검토를 기다리는 상태
    Failed,                                   // 오늘 인증에 실패한 상태
    Success                                   // 오늘 인증에 성공한 상태
}

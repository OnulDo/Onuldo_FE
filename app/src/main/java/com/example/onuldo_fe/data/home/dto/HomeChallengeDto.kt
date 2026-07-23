package com.example.onuldo_fe.data.home.dto

// 홈에 표시할 참여 중인 개인 챌린지 응답
data class HomeChallengeDto(
    val title: String,                    // 챌린지명
    val streakDays: Int,                 // 연속 성공 일수
    val remainingDays: Int,              // 챌린지 종료까지 남은 일수
    val deadlineAt: String,              // 오늘 인증 마감 시각
    val status: String,                  // 오늘 인증 상태
    val verifiedAt: String? = null,      // 인증 완료 시각
    val remainingMinutes: Int? = null,   // 인증 마감까지 남은 시간
    val canVerify: Boolean = true        // 현재 인증 가능 여부
)

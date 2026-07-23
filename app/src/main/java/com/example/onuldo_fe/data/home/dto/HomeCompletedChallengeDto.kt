package com.example.onuldo_fe.data.home.dto

// 오늘 완료한 파티 또는 개인 챌린지 응답
data class HomeCompletedChallengeDto(
    val type: String,                         // 완료 챌린지 유형
    val time: String,                         // 인증 완료 시각 또는 자율
    val title: String,                        // 챌린지명 또는 파티명
    val completedMemberCount: Int? = null,    // 인증 완료 파티원 수
    val totalMemberCount: Int? = null,        // 전체 파티원 수
    val streakDays: Int? = null               // 개인 챌린지 연속 성공 일수
)

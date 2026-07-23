package com.example.onuldo_fe.data.home.dto

// 오늘 인증 대상의 완료 수와 전체 수 집계 응답
data class TodayChallengeDto(
    val date: String,            // 오늘 날짜
    val completedCount: Int,     // 성공 확정된 오늘 인증 수
    val totalCount: Int          // 오늘 인증 대상 전체 수
)

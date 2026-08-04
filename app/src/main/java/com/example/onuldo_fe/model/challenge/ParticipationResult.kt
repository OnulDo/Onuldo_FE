package com.example.onuldo_fe.model.challenge

// 챌린지 참여 생성 결과 도메인 모델 (완료 화면 표시용)
data class ParticipationResult(
    val startDate: String,
    val endDate: String,
    val durationWeeks: Int,
    val durationDays: Int,
    val depositAmount: Int,
    val expectedRefundAmount: Int
)

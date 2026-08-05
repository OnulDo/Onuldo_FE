package com.example.onuldo_fe.data.challenge.dto

// POST /api/challenges/{challengeId}/participations 요청 바디
data class ParticipationRequestDto(
    val depositAmount: Int,
    val durationWeeks: Int
)

// 위 API 응답의 result — 참여 생성 결과(기간/도전금/예상 환급액)
data class ParticipationResultDto(
    val startDate: String = "",
    val endDate: String = "",
    val durationWeeks: Int = 0,
    val durationDays: Int = 0,
    val depositAmount: Int = 0,
    val expectedRefundAmount: Int = 0
)

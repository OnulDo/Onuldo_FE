package com.example.onuldo_fe.data.challenge.dto

// GET /api/users/me/challenges/daily/completed — 오늘 인증까지 완료한 항목 (파티/개인 분리)
data class DailyCompletedResultDto(
    val parties: List<DailyCompletedPartyDto> = emptyList(),
    val challenges: List<DailyCompletedChallengeDto> = emptyList()
)

// 파티 챌린지 완료 항목 — 총 참여 인원 / 오늘 인증 완료 인원 포함, verifiedAt 빠른 순
data class DailyCompletedPartyDto(
    val partyId: Long = 0,
    val partyName: String = "",
    val challengeId: Long = 0,
    val verifiedAt: String = "",                    // "yyyy-MM-dd'T'HH:mm:ss" 오늘 실제 인증 시각
    val totalMemberCount: Int = 0,
    val verifiedMemberCount: Int = 0
)

// 개인 챌린지 완료 항목 — 연속 성공 일수(streakDays) 포함, verifiedAt 빠른 순
data class DailyCompletedChallengeDto(
    val participationId: Long = 0,
    val challengeId: Long = 0,
    val challengeName: String = "",
    val verifiedAt: String = "",                    // 오늘 실제 인증 시각
    val streakDays: Int = 0
)

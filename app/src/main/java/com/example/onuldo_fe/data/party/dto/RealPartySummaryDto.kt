package com.example.onuldo_fe.data.party.dto

/** GET /api/parties 실제 응답의 content 항목. */
data class RealPartySummaryDto(
    val partyId: Long,                    // 파티 ID
    val name: String,                     // 파티 이름
    val challengeTitle: String,           // 연결된 챌린지 이름
    val status: String,                   // 진행 상태
    val endDate: String,                  // 파티 종료일(yyyy-MM-dd)
    val verificationDeadline: String,     // 오늘 인증 마감 시간(HH:mm:ss)
    val progressRate: Double,             // 진행률
    val verifiedMemberCount: Int,         // 오늘 인증 완료 인원
    val totalMemberCount: Int             // 전체 인원
)

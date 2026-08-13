package com.example.onuldo_fe.data.party.dto

/** GET /api/parties/{partyId}/feed 응답의 result. */
data class PartyFeedDto(
    val partyId: Long,                         // 파티 ID
    val name: String,                          // 파티 이름
    val challengeTitle: String,                // 챌린지 이름
    val progressRate: Double,                  // 오늘 인증 진행률
    val verifiedMemberCount: Int,              // 오늘 인증 완료 인원
    val totalMemberCount: Int,                 // 전체 파티원 수
    val members: List<PartyFeedItemDto>         // 파티원별 오늘 인증 현황
)

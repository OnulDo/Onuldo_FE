package com.example.onuldo_fe.data.party.dto

/** GET /api/parties/{partyId}/feed 응답의 result. */
data class PartyFeedDto(
    val partyId: Long,
    val name: String,
    val challengeTitle: String,
    val progressRate: Double,
    val verifiedMemberCount: Int,
    val totalMemberCount: Int,
    val members: List<PartyFeedItemDto>
)

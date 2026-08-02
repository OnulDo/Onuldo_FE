package com.example.onuldo_fe.data.party.dto

/** GET /api/parties 실제 응답의 result 항목. */
data class RealPartySummaryDto(
    val partyId: Long,
    val name: String,
    val status: String,
    val dDay: Int,
    val progressRate: Double,
    val verifiedToday: Int,
    val totalMembers: Int
)

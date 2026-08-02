package com.example.onuldo_fe.data.party.dto

/** GET /api/parties 실제 응답의 result 항목. */
data class RealPartySummaryDto(
    val partyId: Long,           //파티 ID
    val name: String,           //파티 이름
    val status: String,         //진행 상태
    val dDay: Int,               //남은 일수
    val progressRate: Double,   //진행률
    val verifiedToday: Int,     //오늘 인증 인원
    val totalMembers: Int       //전체 인원
)

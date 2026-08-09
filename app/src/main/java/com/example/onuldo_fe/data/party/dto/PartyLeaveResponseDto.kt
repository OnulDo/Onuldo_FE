package com.example.onuldo_fe.data.party.dto

/** POST /api/parties/{partyId}/leave 응답의 result. */
data class PartyLeaveResponseDto(
    val partyId: Long,          // 이탈한 파티 ID
    val dissolved: Boolean,     // 마지막 파티원 이탈로 파티가 해체됐는지 여부
    val newHostUserId: Long?    // 방장 이탈 시 승계받은 회원 ID, 해체 시 null
)

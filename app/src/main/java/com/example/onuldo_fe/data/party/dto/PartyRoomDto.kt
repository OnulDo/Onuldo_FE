package com.example.onuldo_fe.data.party.dto

/** POST /api/parties 요청. */
data class CreatePartyRequestDto(
    val name: String,
    val challengeId: Long,
    val durationDays: Int,
    val depositAmount: Int,
    val maxMembers: Int
)

/** POST /api/parties 응답의 result. */
data class CreatePartyResponseDto(
    val partyId: Long,
    val name: String,
    val inviteCode: String,
    val inviteExpiresAt: String,
    val status: String,
    val hostUserId: Long,
    val maxMembers: Int,
    val createdAt: String
)

/** POST /api/parties/join 요청. */
data class PartyJoinRequestDto(
    val inviteCode: String
)

/** 대기방 응답에 포함되는 파티원. */
data class PartyMemberDto(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val role: String,
    val status: String
)

/** 대기방 조회·참여·준비 상태 토글 응답의 result. */
data class PartyWaitingRoomDto(
    val partyId: Long,
    val name: String,
    val goal: String?,
    val status: String,
    val inviteCode: String,
    val currentMembers: Int,
    val maxMembers: Int,
    val durationDays: Int,
    val depositAmount: Int,
    val members: List<PartyMemberDto>,
    val isHost: Boolean,
    val canStart: Boolean
)

/** GET /api/parties 응답의 result 항목. */
data class PartySummaryDto(
    val partyId: Long,
    val name: String,
    val goal: String,
    val deadline: String?,
    val status: String,
    val dDay: Int,
    val progressRate: Double,
    val verifiedToday: Int,
    val totalMembers: Int
)

/** POST /api/parties/{partyId}/start 응답의 result. */
data class PartyStartResponseDto(
    val partyId: Long,
    val status: String,
    val startTriggeredAt: String
)

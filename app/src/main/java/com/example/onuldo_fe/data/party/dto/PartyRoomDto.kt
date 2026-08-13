package com.example.onuldo_fe.data.party.dto

/** 파티 생성 요청. */
data class CreatePartyRequestDto(
    val name: String,
    val challengeId: Long,
    val durationWeeks: Int,
    val depositAmount: Int,
    val maxMembers: Int
)

/** 파티 생성 응답의 result. */
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

/** 초대 코드 파티 참여 요청. */
data class PartyJoinRequestDto(
    val inviteCode: String
)

/** 파티원 준비 상태 변경 요청. */
data class PartyReadinessRequestDto(
    val ready: Boolean
)

/** 파티 시작 응답의 result. */
data class PartyStartResponseDto(
    val partyId: Long,
    val status: String,
    val startTriggeredAt: String
)

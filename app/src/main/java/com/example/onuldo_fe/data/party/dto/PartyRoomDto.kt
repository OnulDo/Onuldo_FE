package com.example.onuldo_fe.data.party.dto

/** POST /api/parties 요청. */
data class CreatePartyRequestDto(
    val name: String,                          // 파티 이름
    val challengeId: Long,                    // 서버에 등록된 챌린지 ID
    val durationWeeks: Int,                   // 파티 진행 기간(주)
    val depositAmount: Int,                   // 파티원 1인당 도전금
    val maxMembers: Int                       // 파티장을 포함한 최대 모집 인원
)

/** POST /api/parties 응답의 result. */
data class CreatePartyResponseDto(
    val partyId: Long,                        // 생성된 파티 ID
    val name: String,                         // 생성된 파티 이름
    val inviteCode: String,                   // 서버가 발급한 초대 코드
    val inviteExpiresAt: String,              // 초대 코드 만료 시각
    val status: String,                       // 생성 직후 파티 상태
    val hostUserId: Long,                     // 파티장 사용자 ID
    val maxMembers: Int,                      // 최대 모집 인원
    val createdAt: String                     // 파티 생성 시각
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
    val partyId: Long,                        // 시작된 파티 ID
    val status: String,                       // 시작 후 파티 상태(ONGOING)
    val startTriggeredAt: String              // 서버에서 시작 처리가 완료된 시각
)

package com.example.onuldo_fe.data.party.dto

/**
 * GET /api/parties/{partyId}/waiting-room 응답의 result.
 *
 * 화면에 서버 DTO를 직접 전달하지 않고 Repository에서 PartyWaitingRoom으로 변환
 * Swagger 응답에는 챌린지 이름이 없으므로 화면 모델에는 빈 문자열로 전달
 */
data class RealPartyWaitingRoomDto(
    val partyId: Long,                       // 파티 ID
    val name: String,                        // 파티 이름
    val status: String,                      // 진행 상태
    val inviteCode: String,                  // 초대 코드
    val currentMembers: Int,                 // 현재 참여 인원
    val maxMembers: Int,                     // 최대 참여 인원
    val durationDays: Int,                   // 파티 진행 기간
    val depositAmount: Int,                  // 1인당 예치금
    val members: List<RealPartyMemberDto>,   // 대기방 참여자 목록
    val isHost: Boolean,                     // 로그인 사용자의 파티장 여부
    val canStart: Boolean                    // 파티 시작 가능 여부
)

/** 대기방 응답에 포함되는 참여자 한 명의 서버 DTO. */
data class RealPartyMemberDto(
    val userId: Long,                        // 사용자 ID
    val nickname: String,                    // 사용자 닉네임
    val profileImageUrl: String?,            // 프로필 이미지 URL
    val role: String,                        // 파티 역할(HOST, MEMBER)
    val status: String                       // 준비 상태(WAITING, READY)
)

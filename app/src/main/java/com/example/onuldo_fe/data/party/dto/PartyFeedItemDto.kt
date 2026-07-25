package com.example.onuldo_fe.data.party.dto

// 파티원 한 명의 프로필과 오늘 인증 정보 응답
data class PartyFeedItemDto(
    val memberId: String,                      // 파티원 고유 ID
    val nickname: String,                      // 파티원 닉네임
    val profileImageUrl: String?,              // 파티원 프로필 이미지 URL
    val verificationImageUrl: String?,         // 인증 이미지 URL, 미인증이면 null
    val verifiedElapsedMinutes: Int?,           // 인증 후 경과 시간(분), 미인증이면 null
    val defaultCharacterId: Int? = null         // 프로필 이미지가 없을 때 표시할 기본 캐릭터 ID
)

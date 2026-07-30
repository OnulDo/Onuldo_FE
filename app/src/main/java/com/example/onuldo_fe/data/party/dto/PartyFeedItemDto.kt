package com.example.onuldo_fe.data.party.dto

/** 파티 피드 응답에 포함되는 파티원별 오늘 인증 정보. */
data class PartyFeedItemDto(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val isVerifiedToday: Boolean,
    val verificationPhotoUrl: String?,
    val verifiedAt: String?
)

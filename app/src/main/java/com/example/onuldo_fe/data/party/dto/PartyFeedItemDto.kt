package com.example.onuldo_fe.data.party.dto

/** 파티 피드 응답에 포함되는 파티원별 오늘 인증 정보. */
data class PartyFeedItemDto(
    val userId: Long,                          // 사용자 ID
    val nickname: String,                      // 사용자 닉네임
    val profileImageUrl: String?,              // 프로필 이미지 URL
    val isVerifiedToday: Boolean,              // 오늘 인증 완료 여부
    val verificationPhotoUrl: String?,         // 오늘 인증 사진 URL
    val verifiedAt: String?                    // 오늘 인증 완료 시각
)

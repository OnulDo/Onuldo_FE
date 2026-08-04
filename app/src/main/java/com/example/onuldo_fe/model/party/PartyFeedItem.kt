package com.example.onuldo_fe.model.party

// 파티 피드의 파티원별 오늘 인증 정보
data class PartyFeedItem(
    val memberId: String,                      // 파티원 고유 ID
    val nickname: String,                      // 파티원 닉네임
    val profileImageUrl: String?,              // 파티원 프로필 이미지 URL
    val verificationImageUrl: String?,         // 오늘 제출한 인증 이미지 URL
    val isVerifiedToday: Boolean,              // 오늘 인증 완료 여부
    val verifiedElapsedMinutes: Int?           // 인증 완료 후 경과 시간(분), 미인증이면 null
)

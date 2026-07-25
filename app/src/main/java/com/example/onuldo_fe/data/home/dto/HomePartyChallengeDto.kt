package com.example.onuldo_fe.data.home.dto

// 홈에 표시할 참여 중인 파티 챌린지 응답
data class HomePartyChallengeDto(
    val title: String,                                 // 파티명
    val subtitle: String,                              // 연계 챌린지명
    val remainingDays: Int,                           // 파티 종료까지 남은 일수
    val deadlineAt: String,                           // 오늘 인증 마감 시각
    val completedMemberCount: Int,                    // 오늘 인증 성공 파티원 수
    val totalMemberCount: Int,                        // 전체 파티원 수
    val status: String = "NEED_CERTIFICATION",       // 오늘 인증 상태
    val verifiedAt: String? = null,                   // 인증 완료 시각
    val remainingMinutes: Int? = null,                // 인증 마감까지 남은 시간
    val canVerify: Boolean = true                     // 현재 인증 가능 여부
)

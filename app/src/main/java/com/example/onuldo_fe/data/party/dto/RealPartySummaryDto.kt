package com.example.onuldo_fe.data.party.dto

/** GET /api/parties 실제 응답의 content 항목. */
data class RealPartySummaryDto(
    val partyId: Long,                    // 파티 ID
    val name: String,                     // 파티 이름
    val challengeTitle: String,           // 연결된 챌린지 이름
    val goal: String,                     // 파티 카드에 표시할 목표 문구
    val status: String,                   // WAITING / ONGOING / FINISHED / DISSOLVED
    val myStatus: String,                 // NOT_VERIFIED / PENDING / SUCCESS / FAIL
    val endDate: String,                  // 파티 종료일(yyyy-MM-dd)
    val dDay: Int,                        // 서버가 계산한 종료일까지의 남은 일수
    val verificationDeadline: String,     // 오늘 인증 마감 시간(HH:mm:ss)
    val progressRate: Double,             // 진행률
    val verifiedMemberCount: Int,         // 오늘 인증 완료 인원
    val totalMemberCount: Int,            // 전체 인원
    val members: List<PartyListMemberDto> // 파티원별 오늘 인증 현황
)

/** GET /api/parties 카드에 표시할 파티원 정보. */
data class PartyListMemberDto(
    val userId: Long,                    // 회원 ID
    val nickname: String,                // 회원 닉네임
    val profileImageUrl: String,         // 회원이 선택한 프로필 이미지 URL
    val isVerifiedToday: Boolean         // 오늘 인증 완료 여부
)

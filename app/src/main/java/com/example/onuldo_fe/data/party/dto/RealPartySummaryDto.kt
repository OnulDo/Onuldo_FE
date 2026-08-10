package com.example.onuldo_fe.data.party.dto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

/** GET /api/parties 실제 응답의 content 항목. */
data class RealPartySummaryDto(
    val partyId: Long,                    // 파티 ID
    val challengeId: Long = 0,            // 연계된 챌린지 ID
    val name: String,                     // 파티 이름
    val challengeTitle: String,           // 연결된 챌린지 이름
    val goal: String,                     // 파티 카드에 표시할 목표 문구
    val status: String,                   // WAITING / ONGOING / FINISHED / DISSOLVED
    val myStatus: String? = null,         // 구버전 응답의 내 오늘 인증 상태
    @SerializedName(value = "myDailyStatus", alternate = ["MyDailyStatus"])
    val myDailyStatus: String? = null,     // 오늘 나의 데일리 인증 가능 상태
    val endDate: String,                  // 파티 종료일(yyyy-MM-dd)
    val dDay: Int,                        // 서버가 계산한 종료일까지의 남은 일수
    val verificationDeadline: JsonElement? = null, // 오늘 인증 마감 시간(HH:mm:ss 또는 LocalTime 객체)
    val progressRate: Double,             // 진행률
    val verifiedMemberCount: Int,         // 오늘 인증 완료 인원
    val totalMemberCount: Int,            // 전체 인원
    val members: List<PartyListMemberDto> // 파티원별 오늘 인증 현황
)

/** GET /api/parties 카드에 표시할 파티원 정보. */
data class PartyListMemberDto(
    val userId: Long,                    // 회원 ID
    val nickname: String,                // 회원 닉네임
    val profileImageUrl: String? = null, // 회원이 선택한 프로필 이미지 URL
    val isVerifiedToday: Boolean         // 오늘 인증 완료 여부
)

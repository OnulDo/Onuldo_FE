package com.example.onuldo_fe.data.party.dto

// 파티 전체의 오늘 인증 진행 현황 응답
data class PartyProgressDto(
    val completedMemberCount: Int,             // 오늘 인증을 완료한 파티원 수
    val totalMemberCount: Int                  // 현재 참여 중인 전체 파티원 수
)

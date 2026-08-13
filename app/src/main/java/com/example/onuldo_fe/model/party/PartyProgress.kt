package com.example.onuldo_fe.model.party

// 오늘 파티원 인증 완료 비율 계산에 사용할 인원 현황
data class PartyProgress(
    val progressRate: Double,                  // 서버가 계산한 오늘 인증 진행률(0.0~1.0)
    val completedMemberCount: Int,             // 오늘 인증을 완료한 파티원 수
    val totalMemberCount: Int                  // 현재 참여 중인 전체 파티원 수
)

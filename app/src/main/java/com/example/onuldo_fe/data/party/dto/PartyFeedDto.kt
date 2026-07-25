package com.example.onuldo_fe.data.party.dto

// 파티 피드 화면에 필요한 파티 정보와 인증 현황 응답
data class PartyFeedDto(
    val partyId: String,                       // 파티 고유 ID
    val partyName: String,                     // 파티 이름
    val challengeName: String,                 // 연계된 챌린지 이름
    val progress: PartyProgressDto,            // 오늘의 파티 인증 진행 정보
    val items: List<PartyFeedItemDto>           // 파티원별 인증 피드 목록
)

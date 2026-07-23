package com.example.onuldo_fe.model.party

// 파티 피드 화면에 표시할 파티 정보와 오늘 인증 목록
data class PartyFeed(
    val partyId: String,                       // 파티 고유 ID
    val partyName: String,                     // 파티 이름
    val challengeName: String,                 // 연계된 챌린지 이름
    val progress: PartyProgress,               // 오늘 파티원 인증 진행 현황
    val items: List<PartyFeedItem>             // 파티원별 오늘 인증 목록
)

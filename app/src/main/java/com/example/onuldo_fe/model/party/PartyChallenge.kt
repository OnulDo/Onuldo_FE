package com.example.onuldo_fe.model.party

// 파티에서 선택할 수 있는 챌린지 정보
data class PartyChallenge(
    val id: String,                 // 챌린지 고유 ID
    val imageUrl: String,           // 챌린지 대표 이미지 URL
    val title: String,              // 챌린지명
    val participantCount: Int,      // 총 참여 인원
    val period: String,             // 진행 기간
    val deposit: Int                // 1인 도전금
)

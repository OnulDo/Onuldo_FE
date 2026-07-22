package com.example.onuldo_fe.data.party.dto

// 파티에서 선택할 수 있는 챌린지 목록 응답
data class PartyChallengeDto(
    val id: String,
    val imageUrl: String,
    val title: String,
    val participantCount: Int,
    val period: String,
    val deposit: Int
)

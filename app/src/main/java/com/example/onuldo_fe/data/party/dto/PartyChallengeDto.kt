package com.example.onuldo_fe.data.party.dto

// 파티에서 선택할 수 있는 챌린지 목록 응답
data class PartyChallengeDto(
    val id: String,                            // 챌린지 고유 ID
    val imageUrl: String,                      // 챌린지 대표 이미지 URL
    val title: String,                         // 챌린지 이름
    val participantCount: Int,                 // 챌린지 누적 참여 인원
    val category: String,                      // 챌린지 카테고리
    val summary: String,                       // 챌린지 상세 요약
    val benefits: List<String>,                // 챌린지 기대 효과 목록
    val recommendations: List<String>,         // 챌린지 추천 대상 목록
    val verificationInstruction: String,       // 인증 사진 촬영 방법
    val verificationImageUrl: String?          // 인증 예시 이미지 URL
)

package com.example.onuldo_fe.model.party

// 파티에서 선택할 수 있는 챌린지 정보
data class PartyChallenge(
    val id: String,                            // 챌린지 고유 ID
    val imageUrl: String,                      // 챌린지 대표 이미지 URL
    val title: String,                         // 챌린지 이름
    val participantCount: Int,                 // 챌린지 누적 참여 인원
    val category: String,                      // 챌린지 카테고리
    val summary: String,                       // 챌린지 소개 문구
    val benefits: List<String>,                // 챌린지를 통해 얻을 수 있는 효과 목록
    val recommendations: List<String>,         // 챌린지 추천 대상 목록
    val verificationInstruction: String,       // 인증 사진 촬영 방법
    val verificationImageUrl: String?          // 인증 예시 이미지 URL
)

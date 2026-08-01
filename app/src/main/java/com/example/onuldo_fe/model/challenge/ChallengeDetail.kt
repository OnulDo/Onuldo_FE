package com.example.onuldo_fe.model.challenge

// 챌린지 상세 화면(DetailScreen)에서 사용하는 도메인 모델
// GET /api/challenges/{challengeId}
data class ChallengeDetail(
    val id: Long,
    val title: String,               // name
    val participantCount: Int,
    val category: ChallengeCategory,
    val summary: String,             // explainContent — "이 챌린지는?" 본문
    val verificationDescription: String,  // verifyMethodContent — 인증 방법 안내
    val verificationExampleImageUrl: String? = null  // verificationExamplePhotoUrl
)

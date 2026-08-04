package com.example.onuldo_fe.model.challenge

// 챌린지 상세 화면(DetailScreen)에서 사용하는 도메인 모델
// GET /api/challenges/{challengeId}
data class ChallengeDetail(
    val id: Long,
    val title: String,               // name
    val summary: String,             // explainContent — 한 줄 설명
    val participantCount: Int,
    val category: ChallengeCategory,
    val timeStart: String,           // timeStart — 인증 시작 시각 "HH:mm:ss" (없으면 "")
    val timeEnd: String,             // timeEnd — 인증 마감 시각 "HH:mm:ss" (없으면 "")
    val content: List<ContentBlock>, // description(블록 JSON)을 파싱한 본문 블록들
    val verificationDescription: String,  // verifyMethodContent — 인증 방법 안내
    val verificationExampleImageUrl: String? = null,  // verificationExamplePhotoUrl
    val successConditions: List<String> = emptyList(),  // successConditionList — 인증 유의사항 시트
    val failureConditions: List<String> = emptyList()   // failureConditionList
)

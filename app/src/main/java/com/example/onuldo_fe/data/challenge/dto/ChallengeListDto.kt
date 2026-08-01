package com.example.onuldo_fe.data.challenge.dto

// GET /api/challenges 응답의 result 페이로드
// 여기서는 result 안쪽(챌린지 목록 + 페이징 정보)만 정의한다.
data class ChallengeListResultDto(
    val challenges: List<ChallengeItemDto> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val hasNext: Boolean = false
)

// 목록 아이템 — 명세의 모든 필드를 받아두고, 목록 화면에는 필요한 것만 매핑한다.
// (verifyMethodContent, successConditionList 등은 상세/참여 화면용으로 남겨둠)
data class ChallengeItemDto(
    val id: Long = 0,
    val name: String = "",
    val explainContent: String? = null,
    val description: String? = null,
    val captionImgUrl: String? = null,
    val verifyMethodContent: String? = null,
    val verificationExamplePhotoUrl: String? = null,
    val participantCount: Int = 0,
    val category: String = "",
    val timeStart: LocalTimeDto? = null,
    val timeEnd: LocalTimeDto? = null,
    val durationOptionList: List<Int>? = null,
    val depositOptionList: List<Int>? = null,
    val successConditionList: List<String>? = null,
    val failureConditionList: List<String>? = null,
    val verificationLabelList: List<String>? = null
)

// 서버 LocalTime
data class LocalTimeDto(
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val nano: Int = 0
)

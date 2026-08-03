package com.example.onuldo_fe.data.challenge.dto

// GET /api/challenges 목록 응답 (커서 페이지네이션)
data class ChallengeListResultDto(
    val content: List<ChallengeItemDto> = emptyList(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false
)

// 목록/상세 공통 아이템 — 명세의 모든 필드를 받아두고, 화면별로 필요한 것만 매핑한다.
// description은 블록 배열, timeStart/timeEnd는 "HH:mm:ss" 문자열로 내려온다.
data class ChallengeItemDto(
    val id: Long = 0,
    val name: String = "",
    val explainContent: String? = null,
    val description: List<ContentBlockDto>? = null,
    val captionImgUrl: String? = null,
    val verifyMethodContent: String? = null,
    val verificationExamplePhotoUrl: String? = null,
    val participantCount: Int = 0,
    val category: String = "",
    val timeStart: String? = null,
    val timeEnd: String? = null,
    val durationOptionList: List<Int>? = null,
    val depositOptionList: List<Int>? = null,
    val successConditionList: List<String>? = null,
    val failureConditionList: List<String>? = null,
    val verificationLabelList: List<String>? = null
)

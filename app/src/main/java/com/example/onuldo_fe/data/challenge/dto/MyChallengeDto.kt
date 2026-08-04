package com.example.onuldo_fe.data.challenge.dto

// GET /api/users/me/challenges — 내 챌린지 참여 목록 (커서 페이지네이션)
// 최상위에 content/nextCursor/hasNext가 오는 구조라 갤러리(ChallengeListResultDto)와 동일 패턴.
data class MyChallengeListResultDto(
    val content: List<MyChallengeItemDto> = emptyList(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false
)
data class MyChallengeItemDto(
    val participationId: Long = 0,
    val participationStatus: String = "",          // ONGOING/SUCCESS/FAIL/CANCELED
    val participationType: String = "",            // PERSONAL/PARTY
    val challengeId: Long = 0,
    val challengeName: String = "",
    val challengeExplainContent: String? = null,
    val challengeDescription: List<ContentBlockDto>? = null,   // 본문 블록(ContentBlockDto 재사용)
    val challengeCaptionImgUrl: String? = null,
    val challengeVerifyMethodContent: String? = null,
    val challengeVerificationExamplePhotoUrl: String? = null,
    val participantCount: Int = 0,
    val category: String = "",
    val timeStart: String? = null,                 // "HH:mm:ss"
    val timeEnd: String? = null,
    val depositAmount: Int = 0,
    val durationWeeks: Int = 0,
    val startDate: String = "",                     // "yyyy-MM-dd"
    val endDate: String = ""
)

package com.example.onuldo_fe.data.challenge.dto

// 오늘의 챌린지 항목. /users/me/challenges 항목과 대부분 동일하나
// 본문 블록(challengeDescription)이 없고, 해당 날짜 인증 여부(verifiedOnDate)가 추가된다.
data class DailyChallengeItemDto(
    val participationId: Long = 0,
    val participationStatus: String = "",          // ONGOING
    val participationType: String = "",            // PERSONAL/PARTY
    val challengeId: Long = 0,
    val challengeName: String = "",
    val challengeExplainContent: String? = null,
    val challengeCaptionImgUrl: String? = null,
    val challengeVerifyMethodContent: String? = null,
    val participantCount: Int = 0,
    val category: String = "",
    val timeStart: String? = null,                 // "HH:mm:ss"
    val timeEnd: String? = null,
    val depositAmount: Int = 0,
    val durationWeeks: Int = 0,
    val startDate: String = "",                     // "yyyy-MM-dd"
    val endDate: String = "",
    val dailyStatus: String? = null,               // 오늘 인증 상태(WAITING 등) — 서버 신규 필드
    val verifiedOnDate: Boolean = false,           // 오늘 날짜 인증 완료 여부
    val streakDays: Int? = null                    // 연속 인증 성공 일수 — 서버 신규 필드
)

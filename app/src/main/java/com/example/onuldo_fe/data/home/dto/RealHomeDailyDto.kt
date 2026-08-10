package com.example.onuldo_fe.data.home.dto

/** 오늘 수행할 챌린지 한 건의 서버 DTO. */
data class RealHomeDailyChallengeDto(
    val participationId: Long = 0,                  // 챌린지 참여 기록 ID
    val participationStatus: String = "",          // ONGOING
    val participationType: String = "",            // PERSONAL
    val challengeId: Long = 0,                      // 챌린지 ID
    val challengeName: String = "",                // 챌린지 이름
    val challengeExplainContent: String? = null,    // 챌린지 한 줄 설명
    val challengeCaptionImgUrl: String? = null,     // 챌린지 대표 이미지
    val challengeVerifyMethodContent: String? = null, // 인증 방법
    val participantCount: Int = 0,                  // 해당 챌린지 전체 참여자 수
    val category: String = "",                     // 챌린지 카테고리
    val timeStart: String? = null,                  // 오늘 인증 시작 시각
    val timeEnd: String? = null,                    // 오늘 인증 마감 시각
    val depositAmount: Int = 0,                     // 도전금
    val durationWeeks: Int = 0,                     // 진행 기간(주)
    val startDate: String = "",                    // 챌린지 시작일
    val endDate: String = "",                      // 챌린지 종료일
    val dailyStatus: String = "WAITING",           // 오늘 데일리 인증 상태
    val verifiedOnDate: Boolean = false,            // 오늘 인증 기록 존재 여부
    val streakDays: Int = 0                         // 연속 인증 성공 일수
)

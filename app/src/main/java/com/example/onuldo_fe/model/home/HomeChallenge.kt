package com.example.onuldo_fe.model.home

import java.time.LocalTime

// 홈에 표시할 개인 챌린지 정보
data class HomeChallenge(
    val title: String,                         // 챌린지 이름
    val streakDays: Int,                      // 현재 연속 성공 일수
    val remainingDays: Int,                   // 챌린지 종료일까지 남은 일수
    val deadlineAt: LocalTime,                // 오늘 인증 마감 시간
    val status: ChallengeStatus,              // 오늘 인증 상태
    val verifiedAt: LocalTime? = null,        // 인증 완료 시간
    val remainingMinutes: Int? = null,        // 인증 마감까지 남은 시간(분)
    val canVerify: Boolean = true,            // 현재 인증 버튼 활성화 여부
    val challengeId: Long? = null,            // 인증 API에 전달할 챌린지 ID
    val category: String = ""                 // 카메라 TopBar에 표시할 카테고리
)

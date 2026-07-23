package com.example.onuldo_fe.model.home

import java.time.LocalTime

// 홈에 표시할 진행 중인 파티 챌린지 정보
data class HomePartyChallenge(
    val title: String,                         // 파티 이름
    val subtitle: String,                     // 연계된 챌린지 이름
    val remainingDays: Int,                   // 챌린지 종료일까지 남은 일수
    val deadlineAt: LocalTime,                // 오늘 인증 마감 시간
    val completedMemberCount: Int,            // 오늘 인증을 완료한 파티원 수
    val totalMemberCount: Int,                // 현재 참여 중인 전체 파티원 수
    val status: ChallengeStatus = ChallengeStatus.NeedCertification, // 오늘 인증 상태
    val verifiedAt: LocalTime? = null,        // 인증 완료 시간
    val remainingMinutes: Int? = null,        // 인증 마감까지 남은 시간(분)
    val canVerify: Boolean = true             // 현재 인증 버튼 활성화 여부
)

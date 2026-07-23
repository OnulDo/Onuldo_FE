package com.example.onuldo_fe.model.home

// 홈 API 한 번의 응답을 앱에서 사용하는 형태로 묶은 모델
data class HomeData(
    val userName: String = "",                                 // 로그인 사용자 이름
    val todayChallenge: TodayChallenge? = null,                // 오늘 전체 챌린지 진행 현황
    val partyChallenges: List<HomePartyChallenge> = emptyList(), // 진행 중인 파티 챌린지 목록
    val challenges: List<HomeChallenge> = emptyList(),         // 진행 중인 개인 챌린지 목록
    val completedChallenges: List<HomeCompletedChallenge> = emptyList(), // 오늘 완료한 챌린지 목록
    val settlementBanner: SettlementBanner? = null             // 확인하지 않은 파티 정산 결과
)

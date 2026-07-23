package com.example.onuldo_fe.data.home.dto

// 홈 화면에 필요한 데이터를 묶은 전체 API 응답
data class HomeResponseDto(
    val userName: String = "",                                  // 사용자 이름
    val todayChallenge: TodayChallengeDto?,                      // 오늘 챌린지 집계
    val partyChallenges: List<HomePartyChallengeDto>,            // 참여 중인 파티 목록
    val challenges: List<HomeChallengeDto>,                      // 참여 중인 개인 챌린지 목록
    val completedChallenges: List<HomeCompletedChallengeDto>,    // 오늘 완료한 챌린지 목록
    val settlementBanner: SettlementBannerDto? = null            // 미확인 정산 결과 배너
)

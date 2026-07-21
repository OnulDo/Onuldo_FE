package com.example.onuldo_fe.ui.screen.home.data.dummy

import com.example.onuldo_fe.ui.screen.home.data.dto.HomeChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomeCompletedChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomePartyChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomeResponseDto
import com.example.onuldo_fe.ui.screen.home.data.dto.TodayChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.SettlementBannerDto

object HomeDummyData {
    val withChallenges = HomeResponseDto(
        userName = "김민지",
        todayChallenge = TodayChallengeDto("5월 20일 (수)", "오늘의 챌린지", 1, 6),
        partyChallenges = listOf(
            HomePartyChallengeDto("새벽 러너 파티", "30분 러닝", "D-12", "7:00 마감", "45분 남음", 2, 5, remainingMinutes = 45),
            HomePartyChallengeDto("러너 파티", "2시간 러닝", "D-12", "9:00 마감", "", 4, 5)
        ),
        challenges = listOf(
            HomeChallengeDto("30분 러닝", "12일 연속 성공", "D-12", "7:00 마감", "인증하기", "NEED_CERTIFICATION", remainingMinutes = 45),
            HomeChallengeDto("영양제 챙기기", "대기", "D-17", "8:00 마감", "검토대기", "WAITING_REVIEW"),
            HomeChallengeDto("영양제 챙기기", "연속 끊김", "D-17", "8:00 마감", "실패", "FAILED"),
            HomeChallengeDto("새벽 기상", "7일 연속 성공", "D-15", "06:30 인증 완료", "성공", "SUCCESS")
        ),
        completedChallenges = listOf(
            HomeCompletedChallengeDto("06:30", "새벽 러너 파티", "3/3 인증"),
            HomeCompletedChallengeDto("23:00", "밤샘 공부단", "4/4 인증"),
            HomeCompletedChallengeDto("06:30", "새벽 기상", "15일 연속"),
            HomeCompletedChallengeDto("07:00", "30분 러닝", "12일 연속"),
            HomeCompletedChallengeDto("자율", "독서 30분", "8일 연속"),
            HomeCompletedChallengeDto("자율", "1만보 걷기", "5일 연속")
        ),
        settlementBanner = SettlementBannerDto(
            title = "정산이 완료됐어요",
            partyName = "새벽 러너 파티",
            resultId = "settlement-001"
        )
    )

    val empty = HomeResponseDto(
        userName = "김민지",
        todayChallenge = null,
        partyChallenges = emptyList(),
        challenges = emptyList(),
        completedChallenges = emptyList()
    )
}

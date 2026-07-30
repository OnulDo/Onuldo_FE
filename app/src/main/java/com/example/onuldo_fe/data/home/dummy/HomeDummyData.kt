package com.example.onuldo_fe.data.home.dummy

import com.example.onuldo_fe.data.home.dto.HomeChallengeDto
import com.example.onuldo_fe.data.home.dto.HomeCompletedChallengeDto
import com.example.onuldo_fe.data.home.dto.HomePartyChallengeDto
import com.example.onuldo_fe.data.home.dto.HomeResponseDto
import com.example.onuldo_fe.data.home.dto.SettlementBannerDto
import com.example.onuldo_fe.data.home.dto.TodayChallengeDto

object HomeDummyData {
    val withChallenges = HomeResponseDto(
        userName = "김민지",
        todayChallenge = TodayChallengeDto("5월 20일 (수)", 1, 6),
        partyChallenges = listOf(
            HomePartyChallengeDto("새벽 러너 파티", "30분 러닝", 12, "07:00", 2, 5, remainingMinutes = 60),
            HomePartyChallengeDto("러너 파티", "2시간 러닝", 12, "09:00", 4, 5)
        ),
        challenges = listOf(
            HomeChallengeDto("30분 러닝", 12, 12, "07:00", "NEED_CERTIFICATION", remainingMinutes = 90),
            HomeChallengeDto("영양제 챙기기", 12, 17, "08:00", "WAITING_REVIEW"),
            HomeChallengeDto("영양제 챙기기", 0, 17, "08:00", "FAILED"),
            HomeChallengeDto("새벽 기상", 7, 15, "06:30", "SUCCESS", verifiedAt = "06:30")
        ),
        completedChallenges = listOf(
            HomeCompletedChallengeDto("PARTY", "06:30", "새벽 러너 파티", completedMemberCount = 3, totalMemberCount = 3),
            HomeCompletedChallengeDto("PARTY", "23:00", "밤샘 공부단", completedMemberCount = 4, totalMemberCount = 4),
            HomeCompletedChallengeDto("PERSONAL", "06:30", "새벽 기상", streakDays = 15),
            HomeCompletedChallengeDto("PERSONAL", "07:00", "30분 러닝", streakDays = 12),
            HomeCompletedChallengeDto("PERSONAL", "자율", "독서 30분", streakDays = 8),
            HomeCompletedChallengeDto("PERSONAL", "자율", "1만보 걷기", streakDays = 5)
        ),
        settlementBanner = SettlementBannerDto(
            partyName = "새벽 러너 파티",
            resultId = "1"
        )
    )

    val empty = HomeResponseDto(
        userName = "김민지",
        todayChallenge = null,
        partyChallenges = emptyList(),
        challenges = emptyList(),
        completedChallenges = emptyList()
    )

    val allCompleted = HomeResponseDto(
        userName = "김민지",
        todayChallenge = TodayChallengeDto("5월 20일 (수)", 6, 6),
        partyChallenges = emptyList(),
        challenges = emptyList(),
        completedChallenges = listOf(
            HomeCompletedChallengeDto("PARTY", "06:30", "새벽 러너 파티", completedMemberCount = 3, totalMemberCount = 3),
            HomeCompletedChallengeDto("PARTY", "23:00", "밤샘 공부단", completedMemberCount = 4, totalMemberCount = 4),
            HomeCompletedChallengeDto("PERSONAL", "06:30", "새벽 기상", streakDays = 15),
            HomeCompletedChallengeDto("PERSONAL", "07:00", "30분 러닝", streakDays = 12),
            HomeCompletedChallengeDto("PERSONAL", "자율", "독서 30분", streakDays = 8),
            HomeCompletedChallengeDto("PERSONAL", "자율", "1만보 걷기", streakDays = 5)
        )
    )

}

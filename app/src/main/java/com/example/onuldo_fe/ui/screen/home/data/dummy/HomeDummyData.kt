package com.example.onuldo_fe.ui.screen.home.data.dummy

import com.example.onuldo_fe.ui.screen.home.data.dto.HomeChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomeCompletedChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomePartyChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomeResponseDto
import com.example.onuldo_fe.ui.screen.home.data.dto.TodayChallengeDto

// 서버 연결 전 홈 화면 테스트용 fake 응답 데이터
object HomeDummyData {
    val withChallenges = HomeResponseDto(
        todayChallenge = TodayChallengeDto(
            date = "5월 20일 (수)",
            title = "오늘의 챌린지",
            completedCount = 1,
            totalCount = 4
        ),
        partyChallenges = listOf(
            HomePartyChallengeDto(
                title = "새벽 러닝 파티",
                subtitle = "30분 러닝",
                dDay = "D-12",
                deadline = "7:00 마감",
                timeLeft = "1시간 30분 남음",
                completedMemberCount = 2,
                totalMemberCount = 5
            ),
            HomePartyChallengeDto(
                title = "러너 파티",
                subtitle = "2시간 러닝",
                dDay = "D-12",
                deadline = "9:00 마감",
                timeLeft = "",
                completedMemberCount = 4,
                totalMemberCount = 5
            )
        ),
        challenges = listOf(
            HomeChallengeDto(
                title = "30분 러닝",
                subtitle = "12일 연속 성공",
                dDay = "D-12",
                deadline = "7:00 마감",
                actionText = "인증하기",
                status = "NEED_CERTIFICATION"
            ),
            HomeChallengeDto(
                title = "영양제 챙기기",
                subtitle = "대기",
                dDay = "D-17",
                deadline = "8:00 마감",
                actionText = "검토하기",
                status = "WAITING_REVIEW"
            ),
            HomeChallengeDto(
                title = "영양제 챙기기",
                subtitle = "연속 끊김",
                dDay = "D-17",
                deadline = "8:00 마감",
                actionText = "실패",
                status = "FAILED"
            ),
            HomeChallengeDto(
                title = "새벽 기상",
                subtitle = "7일 연속 성공",
                dDay = "D-15",
                deadline = "06:30 인증 완료",
                actionText = "성공",
                status = "SUCCESS"
            )
        ),
        completedChallenges = listOf(
            HomeCompletedChallengeDto(
                time = "06:30",
                title = "새벽 러너 파티",
                resultText = "3/3 인증"
            ),
            HomeCompletedChallengeDto(
                time = "23:00",
                title = "밤샘 공부단",
                resultText = "4/4 인증"
            ),
            HomeCompletedChallengeDto(
                time = "06:30",
                title = "새벽 기상",
                resultText = "15일 연속"
            ),
            HomeCompletedChallengeDto(
                time = "07:00",
                title = "30분 러닝",
                resultText = "12일 연속"
            ),
            HomeCompletedChallengeDto(
                time = "자율",
                title = "독서 30분",
                resultText = "8일 연속"
            ),
            HomeCompletedChallengeDto(
                time = "자율",
                title = "1만보 걷기",
                resultText = "5일 연속"
            )
        )
    )

    val empty = HomeResponseDto(
        todayChallenge = null,
        partyChallenges = emptyList(),
        challenges = emptyList(),
        completedChallenges = emptyList()
    )
}

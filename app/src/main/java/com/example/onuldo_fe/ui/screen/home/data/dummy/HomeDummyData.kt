package com.example.onuldo_fe.ui.screen.home.data.dummy

import com.example.onuldo_fe.ui.screen.home.data.dto.HomeChallengeDto
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
        )
    )

    val empty = HomeResponseDto(
        todayChallenge = null,
        challenges = emptyList()
    )
}

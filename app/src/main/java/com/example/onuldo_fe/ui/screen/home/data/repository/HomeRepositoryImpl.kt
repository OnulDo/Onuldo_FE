package com.example.onuldo_fe.ui.screen.home.data.repository

import com.example.onuldo_fe.ui.screen.home.data.dto.HomeChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomeResponseDto
import com.example.onuldo_fe.ui.screen.home.data.dto.TodayChallengeDto
import com.example.onuldo_fe.ui.screen.home.model.ChallengeStatus
import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge

class HomeRepositoryImpl(
    private val homeResponse: HomeResponseDto
) : HomeRepository {
    override fun getTodayChallenge(): TodayChallenge? {
        return homeResponse.todayChallenge?.toModel()
    }

    override fun getChallenges(): List<HomeChallenge> {
        return homeResponse.challenges.map { it.toModel() }
    }
}

private fun TodayChallengeDto.toModel(): TodayChallenge {
    // API 원본 값으로 오늘의 챌린지 카드 진행률 문구와 비율 생성
    val progress = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount

    return TodayChallenge(
        date = date,
        title = title,
        progressText = "$completedCount/$totalCount 완료",
        progress = progress
    )
}

private fun HomeChallengeDto.toModel(): HomeChallenge {
    // 문자열 status를 UI에서 쓰는 enum으로 변환
    return HomeChallenge(
        title = title,
        subtitle = subtitle,
        dDay = dDay,
        deadline = deadline,
        actionText = actionText,
        status = when (status) {
            "NEED_CERTIFICATION" -> ChallengeStatus.NeedCertification
            "WAITING_REVIEW" -> ChallengeStatus.WaitingReview
            "FAILED" -> ChallengeStatus.Failed
            "SUCCESS" -> ChallengeStatus.Success
            else -> ChallengeStatus.NeedCertification
        }
    )
}

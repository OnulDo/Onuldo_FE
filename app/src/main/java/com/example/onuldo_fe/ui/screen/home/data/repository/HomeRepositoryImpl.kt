package com.example.onuldo_fe.ui.screen.home.data.repository

import com.example.onuldo_fe.ui.screen.home.data.dto.HomeChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomeCompletedChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomePartyChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomeResponseDto
import com.example.onuldo_fe.ui.screen.home.data.dto.TodayChallengeDto
import com.example.onuldo_fe.ui.screen.home.model.ChallengeStatus
import com.example.onuldo_fe.ui.screen.home.model.CompletedChallengeType
import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.screen.home.model.HomeCompletedChallenge
import com.example.onuldo_fe.ui.screen.home.model.HomePartyChallenge
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge

class HomeRepositoryImpl(private val homeResponse: HomeResponseDto) : HomeRepository {
    override fun getTodayChallenge() = homeResponse.todayChallenge?.toModel()
    override fun getPartyChallenges() = homeResponse.partyChallenges.map { it.toModel() }
    override fun getChallenges() = homeResponse.challenges.map { it.toModel() }
    override fun getCompletedChallenges() = homeResponse.completedChallenges.map { it.toModel() }
}

private fun TodayChallengeDto.toModel(): TodayChallenge {
    val progress = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
    return TodayChallenge(date, title, "$completedCount/$totalCount 완료", progress)
}

private fun HomeChallengeDto.toModel() = HomeChallenge(
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

private fun HomePartyChallengeDto.toModel() = HomePartyChallenge(
    title, subtitle, dDay, deadline, timeLeft, completedMemberCount, totalMemberCount
)

private fun HomeCompletedChallengeDto.toModel() = HomeCompletedChallenge(
    time = time,
    title = title,
    resultText = resultText,
    type = if (resultText.contains('/')) CompletedChallengeType.Party else CompletedChallengeType.Personal
)

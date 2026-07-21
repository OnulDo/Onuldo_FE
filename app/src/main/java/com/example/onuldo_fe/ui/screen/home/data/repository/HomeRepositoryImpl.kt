package com.example.onuldo_fe.ui.screen.home.data.repository

import com.example.onuldo_fe.ui.screen.home.data.dto.HomeChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomeCompletedChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomePartyChallengeDto
import com.example.onuldo_fe.ui.screen.home.data.dto.HomeResponseDto
import com.example.onuldo_fe.ui.screen.home.model.ChallengeStatus
import com.example.onuldo_fe.ui.screen.home.model.CompletedChallengeType
import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.screen.home.model.HomeCompletedChallenge
import com.example.onuldo_fe.ui.screen.home.model.HomePartyChallenge
import com.example.onuldo_fe.ui.screen.home.model.SettlementBanner
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge

class HomeRepositoryImpl(private val homeResponse: HomeResponseDto) : HomeRepository {
    private val partyModels by lazy { homeResponse.partyChallenges.map { it.toModel() } }
    private val challengeModels by lazy { homeResponse.challenges.map { it.toModel() } }
    private val completedModels by lazy { homeResponse.completedChallenges.map { it.toModel() } }

    override fun getUserName() = homeResponse.userName

    override fun getTodayChallenge(): TodayChallenge? {
        val source = homeResponse.todayChallenge ?: return null
        val activeTargets = partyModels.map { it.status } + challengeModels.map { it.status }
        val totalCount = if (activeTargets.isNotEmpty()) activeTargets.size else completedModels.size
        val completedCount = if (activeTargets.isNotEmpty()) {
            activeTargets.count { it == ChallengeStatus.Success }
        } else {
            completedModels.size
        }
        val progress = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
        return TodayChallenge(source.date, source.title, "$completedCount/$totalCount 완료", progress, completedCount, totalCount)
    }

    override fun getPartyChallenges() = partyModels
    override fun getChallenges() = challengeModels
    override fun getCompletedChallenges() = completedModels

    override fun getSettlementBanner(): SettlementBanner? = homeResponse.settlementBanner
        ?.takeUnless { it.isChecked }
        ?.let { SettlementBanner(it.title, it.partyName, it.resultId) }
}

private fun String.toChallengeStatus() = when (this) {
    "WAITING_REVIEW" -> ChallengeStatus.WaitingReview
    "FAILED" -> ChallengeStatus.Failed
    "SUCCESS" -> ChallengeStatus.Success
    else -> ChallengeStatus.NeedCertification
}

private fun HomeChallengeDto.toModel() = HomeChallenge(
    title = title,
    subtitle = subtitle,
    dDay = dDay,
    deadline = deadline,
    actionText = actionText,
    status = status.toChallengeStatus(),
    verifiedAt = verifiedAt,
    remainingMinutes = remainingMinutes,
    canVerify = canVerify
)

private fun HomePartyChallengeDto.toModel() = HomePartyChallenge(
    title = title,
    subtitle = subtitle,
    dDay = dDay,
    deadline = deadline,
    timeLeft = timeLeft,
    completedMemberCount = completedMemberCount,
    totalMemberCount = totalMemberCount,
    status = status.toChallengeStatus(),
    actionText = actionText,
    verifiedAt = verifiedAt,
    remainingMinutes = remainingMinutes,
    canVerify = canVerify
)

private fun HomeCompletedChallengeDto.toModel() = HomeCompletedChallenge(
    time = time,
    title = title,
    resultText = resultText,
    type = if (resultText.contains('/')) CompletedChallengeType.Party else CompletedChallengeType.Personal
)

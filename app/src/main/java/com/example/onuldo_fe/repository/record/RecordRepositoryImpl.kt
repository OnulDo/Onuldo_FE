package com.example.onuldo_fe.repository.record

import com.example.onuldo_fe.data.record.api.RecordApi
import com.example.onuldo_fe.data.record.dto.CompletedChallengeDto
import com.example.onuldo_fe.data.record.dto.OngoingChallengeDto
import com.example.onuldo_fe.model.record.ChallengeRecordType
import com.example.onuldo_fe.model.record.CompletedChallenge
import com.example.onuldo_fe.model.record.CompletedRecordSummary
import com.example.onuldo_fe.model.record.CompletedResultStatus
import com.example.onuldo_fe.model.record.OngoingChallenge
import kotlin.math.absoluteValue

class RecordRepositoryImpl(private val api: RecordApi) : RecordRepository {
    override suspend fun getOngoingChallenges(): List<OngoingChallenge> =
        api.getOngoingChallenges().result.map { it.toModel() }

    override suspend fun getCompletedChallenges(): CompletedRecordSummary {
        val result = api.getCompletedChallenges().result
        return CompletedRecordSummary(
            totalCompletedCount = result.totalCompletedCount.coerceAtLeast(0),
            successRate = result.successRate.coerceIn(0, 100),
            totalSavedAmount = result.totalSavedAmount,
            challenges = result.completedChallenges.map { it.toModel() }
        )
    }

    private fun OngoingChallengeDto.toModel() = OngoingChallenge(
        participationId = participationId,
        challengeId = challengeId,
        title = challengeTitle,
        isVerifiedToday = isVerifiedToday,
        daysUntilEnd = daysUntilEnd.coerceAtLeast(0),
        achievementRate = achievementRate.coerceIn(0, 100),
        depositAmount = depositAmount.coerceAtLeast(0),
        type = when (type.uppercase()) {
            "PERSONAL" -> ChallengeRecordType.PERSONAL
            "PARTY" -> ChallengeRecordType.PARTY
            else -> ChallengeRecordType.UNKNOWN
        }
    )

    private fun CompletedChallengeDto.toModel(): CompletedChallenge {
        val status = if (resultStatus.uppercase() == "SUCCESS") {
            CompletedResultStatus.SUCCESS
        } else {
            CompletedResultStatus.FAILURE
        }
        return CompletedChallenge(
            participationId = participationId,
            challengeId = challengeId,
            title = challengeTitle,
            resultStatus = status,
            netAmount = if (status == CompletedResultStatus.SUCCESS) {
                refundAmount.absoluteValue
            } else {
                -refundAmount.absoluteValue
            },
            endedDate = endedDate,
            achievementRate = achievementRate.coerceIn(0, 100)
        )
    }
}
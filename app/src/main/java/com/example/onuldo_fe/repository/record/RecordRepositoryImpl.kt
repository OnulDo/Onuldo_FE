package com.example.onuldo_fe.repository.record

import com.example.onuldo_fe.data.record.api.RecordApi
import com.example.onuldo_fe.data.record.dto.CompletedChallengeDto
import com.example.onuldo_fe.data.record.dto.OngoingChallengeDto
import com.example.onuldo_fe.model.record.ChallengeRecordType
import com.example.onuldo_fe.model.record.CompletedChallenge
import com.example.onuldo_fe.model.record.CompletedRecordSummary
import com.example.onuldo_fe.model.record.CompletedResultStatus
import com.example.onuldo_fe.model.record.OngoingChallenge

class RecordRepositoryImpl(private val api: RecordApi) : RecordRepository {
    override suspend fun getOngoingChallenges(): List<OngoingChallenge> {
        val result = requireNotNull(api.getOngoingChallenges().result) {
            "진행 중 챌린지 응답 결과가 누락되었습니다."
        }
        return result.map { it.toModel() }
    }

    override suspend fun getCompletedChallenges(): CompletedRecordSummary {
        val result = requireNotNull(api.getCompletedChallenges().result) {
            "완료 챌린지 응답 결과가 누락되었습니다."
        }
        val challenges = requireNotNull(result.completedChallenges) {
            "완료 챌린지 목록이 누락되었습니다."
        }
        return CompletedRecordSummary(
            totalCompletedCount = (result.totalCompletedCount ?: challenges.size).coerceAtLeast(0),
            successRate = (result.successRate ?: 0).coerceIn(0, 100),
            totalSavedAmount = result.totalSavedAmount ?: 0,
            challenges = challenges.map { it.toModel() }
        )
    }

    private fun OngoingChallengeDto.toModel(): OngoingChallenge {
        val validParticipationId = participationId.requirePositive("participationId")
        val validChallengeId = challengeId.requirePositive("challengeId")
        val validTitle = challengeTitle.requireText("challengeTitle")

        return OngoingChallenge(
            participationId = validParticipationId,
            challengeId = validChallengeId,
            title = validTitle,
            isVerifiedToday = isVerifiedToday ?: false,
            daysUntilEnd = daysUntilEnd?.coerceAtLeast(0) ?: 0,
            achievementRate = achievementRate?.coerceIn(0, 100) ?: 0,
            depositAmount = depositAmount?.coerceAtLeast(0) ?: 0,
            type = when (type?.uppercase()) {
                "PERSONAL" -> ChallengeRecordType.PERSONAL
                "PARTY" -> ChallengeRecordType.PARTY
                else -> ChallengeRecordType.UNKNOWN
            }
        )
    }

    private fun CompletedChallengeDto.toModel(): CompletedChallenge {
        val validParticipationId = participationId.requirePositive("participationId")
        val validChallengeId = challengeId.requirePositive("challengeId")
        val validTitle = challengeTitle.requireText("challengeTitle")
        val status = when (resultStatus?.uppercase()) {
            "SUCCESS" -> CompletedResultStatus.SUCCESS
            "FAIL", "FAILURE" -> CompletedResultStatus.FAILURE
            else -> error("알 수 없는 완료 결과 상태입니다: $resultStatus")
        }
        val amount = refundAmount ?: 0

        return CompletedChallenge(
            participationId = validParticipationId,
            challengeId = validChallengeId,
            title = validTitle,
            resultStatus = status,
            netAmount = amount,
            endedDate = endedDate.requireText("endedDate"),
            achievementRate = achievementRate?.coerceIn(0, 100) ?: 0
        )
    }

    private fun Long?.requirePositive(fieldName: String): Long {
        val value = requireNotNull(this) { "$fieldName 필드가 누락되었습니다." }
        require(value > 0) { "$fieldName 값은 0보다 커야 합니다." }
        return value
    }

    private fun String?.requireText(fieldName: String): String =
        this?.trim()?.takeIf { it.isNotEmpty() }
            ?: error("$fieldName 필드가 누락되었거나 비어 있습니다.")
}

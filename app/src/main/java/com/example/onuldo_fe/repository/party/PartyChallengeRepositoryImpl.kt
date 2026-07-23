package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyChallengeApi
import com.example.onuldo_fe.data.party.dto.PartyChallengeDto
import com.example.onuldo_fe.model.party.PartyChallenge

// 챌린지 API 응답을 앱 내부 챌린지 모델로 변환
class PartyChallengeRepositoryImpl(
    private val api: PartyChallengeApi
) : PartyChallengeRepository {
    override suspend fun getPartyChallenges(): List<PartyChallenge> =
        api.getPartyChallenges().map(PartyChallengeDto::toModel)
}

private fun PartyChallengeDto.toModel() = PartyChallenge(
    id = id,
    imageUrl = imageUrl,
    title = title,
    participantCount = participantCount,
    category = category,
    summary = summary,
    benefits = benefits,
    recommendations = recommendations,
    verificationInstruction = verificationInstruction,
    verificationImageUrl = verificationImageUrl
)

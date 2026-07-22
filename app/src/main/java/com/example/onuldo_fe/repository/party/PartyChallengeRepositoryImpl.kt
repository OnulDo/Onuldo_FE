package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyChallengeApi
import com.example.onuldo_fe.data.party.dto.PartyChallengeDto
import com.example.onuldo_fe.model.party.PartyChallenge

class PartyChallengeRepositoryImpl(
    private val api: PartyChallengeApi
) : PartyChallengeRepository {
    override fun getPartyChallenges(): List<PartyChallenge> =
        api.getPartyChallenges().map(PartyChallengeDto::toModel)
}

private fun PartyChallengeDto.toModel() = PartyChallenge(
    id = id,
    imageUrl = imageUrl,
    title = title,
    participantCount = participantCount,
    period = period,
    deposit = deposit
)

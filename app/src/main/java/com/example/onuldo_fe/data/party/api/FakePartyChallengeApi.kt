package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.PartyChallengeDto
import com.example.onuldo_fe.data.party.dummy.PartyChallengeDummyData

class FakePartyChallengeApi : PartyChallengeApi {
    override suspend fun getPartyChallenges(): List<PartyChallengeDto> =
        PartyChallengeDummyData.challenges
}

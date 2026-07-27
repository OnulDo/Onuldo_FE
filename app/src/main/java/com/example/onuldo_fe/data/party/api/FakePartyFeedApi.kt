package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.data.party.dummy.FakePartyFeedState
import com.example.onuldo_fe.data.party.dummy.PartyFeedDummyData

class FakePartyFeedApi : PartyFeedApi {
    override suspend fun getPartyFeed(partyId: Long): PartyFeedDto {
        val party = FakePartyFeedState.get(partyId)
        val total = party.memberCount.coerceAtLeast(0)
        val members = PartyFeedDummyData.feedItems.take(total.coerceAtMost(PartyFeedDummyData.feedItems.size))
        val verified = members.count { it.isVerifiedToday }
        return PartyFeedDto(
            partyId = partyId,
            name = party.partyName,
            challengeTitle = party.challengeName,
            progressRate = if (total == 0) 0.0 else verified.toDouble() / total,
            verifiedMemberCount = verified,
            totalMemberCount = total,
            members = members
        )
    }
}

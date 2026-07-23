package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.PartyProgressDto
import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.data.party.dummy.PartyFeedDummyData
import com.example.onuldo_fe.data.party.dummy.FakePartyFeedState

class FakePartyFeedApi : PartyFeedApi {
    override suspend fun getPartyFeed(partyId: String): PartyFeedDto {
        val party = FakePartyFeedState.get(partyId)
        val count = party.memberCount.coerceAtMost(PartyFeedDummyData.feedItems.size)
        val items = when {
            count == 0 -> emptyList()
            count == 1 -> listOf(PartyFeedDummyData.feedItems.last())
            else -> PartyFeedDummyData.feedItems.take(count - 1) + PartyFeedDummyData.feedItems.last()
        }
        return PartyFeedDto(
            partyId = partyId,
            partyName = party.partyName,
            challengeName = party.challengeName,
            progress = PartyProgressDto(
                completedMemberCount = (count - 1).coerceAtLeast(0),
                totalMemberCount = count
            ),
            items = items
        )
    }
}

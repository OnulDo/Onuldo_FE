package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.PartyProgressDto
import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto
import com.example.onuldo_fe.data.party.dummy.PartyFeedDummyData
import com.example.onuldo_fe.data.party.dummy.FakePartyFeedState

class FakePartyFeedApi : PartyFeedApi {
    override fun getPartyProgress(partyId: String): PartyProgressDto {
        val totalCount = FakePartyFeedState.memberCount
        return PartyProgressDto(
            completedMemberCount = (totalCount - 1).coerceAtLeast(0),
            totalMemberCount = totalCount
        )
    }

    override fun getPartyFeedItems(partyId: String): List<PartyFeedItemDto> {
        val count = FakePartyFeedState.memberCount.coerceAtMost(PartyFeedDummyData.feedItems.size)
        if (count == 0) return emptyList()
        if (count == 1) return listOf(PartyFeedDummyData.feedItems.last())
        return PartyFeedDummyData.feedItems.take(count - 1) + PartyFeedDummyData.feedItems.last()
    }
}

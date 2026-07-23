package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.PartyProgressDto
import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto

interface PartyFeedApi {
    fun getPartyProgress(partyId: String): PartyProgressDto
    fun getPartyFeedItems(partyId: String): List<PartyFeedItemDto>
}

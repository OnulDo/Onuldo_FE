package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.PartyFeedDto

interface PartyFeedApi {
    suspend fun getPartyFeed(partyId: String): PartyFeedDto
}

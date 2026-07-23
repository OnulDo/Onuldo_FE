package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.model.party.PartyFeed

interface PartyFeedRepository {
    suspend fun getPartyFeed(partyId: String): PartyFeed
}

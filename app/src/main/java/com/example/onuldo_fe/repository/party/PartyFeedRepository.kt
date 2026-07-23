package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.model.party.PartyProgress
import com.example.onuldo_fe.model.party.PartyFeedItem

interface PartyFeedRepository {
    fun getPartyProgress(partyId: String): PartyProgress
    fun getPartyFeedItems(partyId: String): List<PartyFeedItem>
}

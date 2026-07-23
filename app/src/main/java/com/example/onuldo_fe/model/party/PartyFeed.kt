package com.example.onuldo_fe.model.party

data class PartyFeed(
    val partyId: String,
    val partyName: String,
    val challengeName: String,
    val progress: PartyProgress,
    val items: List<PartyFeedItem>
)

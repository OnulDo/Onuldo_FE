package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyFeedApi
import com.example.onuldo_fe.data.party.api.PartyFeedApi

object PartyFeedRepositoryProvider {
    fun provide(api: PartyFeedApi = FakePartyFeedApi()): PartyFeedRepository =
        PartyFeedRepositoryImpl(api)
}

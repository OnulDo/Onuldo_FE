package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyApi
import com.example.onuldo_fe.data.party.api.PartyApi

object PartyRepositoryProvider {
    fun provide(api: PartyApi = FakePartyApi()): PartyRepository = PartyRepositoryImpl(api)
}

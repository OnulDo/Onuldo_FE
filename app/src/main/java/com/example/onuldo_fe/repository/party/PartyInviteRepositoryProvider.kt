package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyInviteApi
import com.example.onuldo_fe.data.party.api.PartyInviteApi

object PartyInviteRepositoryProvider {
    fun provide(api: PartyInviteApi = FakePartyInviteApi()): PartyInviteRepository =
        PartyInviteRepositoryImpl(api)
}

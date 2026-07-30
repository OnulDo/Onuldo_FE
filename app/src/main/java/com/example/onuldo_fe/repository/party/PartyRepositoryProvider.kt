package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyApi
import com.example.onuldo_fe.data.party.api.PartyApi

// 파티 Repository 구현체와 사용할 API 제공
object PartyRepositoryProvider {
    fun provide(api: PartyApi = FakePartyApi()): PartyRepository = PartyRepositoryImpl(api)
}

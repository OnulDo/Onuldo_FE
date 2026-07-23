package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyFeedApi
import com.example.onuldo_fe.data.party.api.PartyFeedApi

// 파티 피드 Repository 구현체와 사용할 API 제공
object PartyFeedRepositoryProvider {
    fun provide(api: PartyFeedApi = FakePartyFeedApi()): PartyFeedRepository =
        PartyFeedRepositoryImpl(api)
}

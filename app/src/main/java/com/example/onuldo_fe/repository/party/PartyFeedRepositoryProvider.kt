package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyFeedApi
import com.example.onuldo_fe.data.party.api.PartyFeedApi
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.config.PartyApiConfig
import com.example.onuldo_fe.data.network.NetworkModule

// 파티 피드 Repository 구현체와 사용할 API 제공
object PartyFeedRepositoryProvider {
    fun provide(
        fakeApi: PartyFeedApi = FakePartyFeedApi(),
        realApi: RealPartyApi = NetworkModule.create(RealPartyApi::class.java),
        useRealPartyFeedApi: Boolean = PartyApiConfig.USE_REAL_FEED
    ): PartyFeedRepository = PartyFeedRepositoryImpl(
        fakeApi = fakeApi,
        realApi = realApi,
        useRealPartyFeedApi = useRealPartyFeedApi
    )
}

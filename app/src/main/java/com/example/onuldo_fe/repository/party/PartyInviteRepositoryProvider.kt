package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyInviteApi
import com.example.onuldo_fe.data.party.api.PartyInviteApi
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.config.PartyApiConfig
import com.example.onuldo_fe.data.party.network.PartyNetworkClient

// 초대코드 Repository 구현체와 사용할 API 제공
object PartyInviteRepositoryProvider {
    fun provide(
        fakeApi: PartyInviteApi = FakePartyInviteApi(),
        realApi: RealPartyApi = PartyNetworkClient.create(RealPartyApi::class.java),
        useRealPartyJoinApi: Boolean = PartyApiConfig.USE_REAL_JOIN
    ): PartyInviteRepository = PartyInviteRepositoryImpl(fakeApi, realApi, useRealPartyJoinApi)
}

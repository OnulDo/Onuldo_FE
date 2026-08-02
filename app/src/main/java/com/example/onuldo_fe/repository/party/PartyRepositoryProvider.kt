package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyApi
import com.example.onuldo_fe.data.party.api.PartyApi
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.config.PartyApiConfig
import com.example.onuldo_fe.data.party.network.PartyNetworkClient

// 파티 Repository 구현체와 사용할 API 제공
object PartyRepositoryProvider {
    fun provide(
        fakeApi: PartyApi = FakePartyApi(),
        realApi: RealPartyApi = PartyNetworkClient.create(RealPartyApi::class.java),
        useRealPartyListApi: Boolean = PartyApiConfig.USE_REAL_LIST,
        useRealPartyWaitingRoomApi: Boolean = PartyApiConfig.USE_REAL_WAITING_ROOM,
        useRealPartyCreateApi: Boolean = PartyApiConfig.USE_REAL_CREATE,
        useRealPartyReadyApi: Boolean = PartyApiConfig.USE_REAL_READY
    ): PartyRepository = PartyRepositoryImpl(
        fakeApi = fakeApi,
        realApi = realApi,
        useRealPartyListApi = useRealPartyListApi,
        useRealPartyWaitingRoomApi = useRealPartyWaitingRoomApi,
        useRealPartyCreateApi = useRealPartyCreateApi,
        useRealPartyReadyApi = useRealPartyReadyApi
    )
}

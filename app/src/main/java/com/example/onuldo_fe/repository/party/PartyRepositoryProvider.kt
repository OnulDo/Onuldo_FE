package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.network.NetworkModule

// 파티 Repository 구현체와 사용할 API 제공
object PartyRepositoryProvider {
    fun provide(
        realApi: RealPartyApi = NetworkModule.create(RealPartyApi::class.java)
    ): PartyRepository = PartyRepositoryImpl(realApi)
}

package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyInviteApi
import com.example.onuldo_fe.data.party.api.PartyInviteApi

// 초대코드 Repository 구현체와 사용할 API 제공
object PartyInviteRepositoryProvider {
    fun provide(api: PartyInviteApi = FakePartyInviteApi()): PartyInviteRepository =
        PartyInviteRepositoryImpl(api)
}

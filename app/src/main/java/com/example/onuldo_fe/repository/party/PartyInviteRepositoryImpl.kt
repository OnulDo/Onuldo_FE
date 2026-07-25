package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyInviteApi
import com.example.onuldo_fe.model.party.PartyJoinResult

// 초대코드 참여 요청을 API에 전달하고 참여 결과 반환
class PartyInviteRepositoryImpl(
    private val api: PartyInviteApi
) : PartyInviteRepository {
    override suspend fun joinParty(inviteCode: String): PartyJoinResult = api.joinParty(inviteCode)
}

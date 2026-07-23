package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyInviteApi
import com.example.onuldo_fe.model.party.PartyJoinResult

class PartyInviteRepositoryImpl(
    private val api: PartyInviteApi
) : PartyInviteRepository {
    override fun joinParty(inviteCode: String): PartyJoinResult = api.joinParty(inviteCode)
}

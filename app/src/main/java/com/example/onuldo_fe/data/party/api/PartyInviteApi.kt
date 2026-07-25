package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.model.party.PartyJoinResult

interface PartyInviteApi {
    suspend fun joinParty(inviteCode: String): PartyJoinResult
}

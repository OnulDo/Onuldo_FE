package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.model.party.PartyJoinResult

interface PartyInviteRepository {
    suspend fun joinParty(inviteCode: String): PartyJoinResult
}

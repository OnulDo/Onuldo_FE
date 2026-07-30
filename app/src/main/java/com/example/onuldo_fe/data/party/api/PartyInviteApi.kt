package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.PartyJoinRequestDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto

interface PartyInviteApi {
    suspend fun joinParty(request: PartyJoinRequestDto): PartyWaitingRoomDto
}

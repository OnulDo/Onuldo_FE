package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import com.example.onuldo_fe.data.party.dto.PartySummaryDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto

interface PartyApi {
    suspend fun getParties(): List<PartySummaryDto>
    suspend fun createParty(request: CreatePartyRequestDto): CreatePartyResponseDto
    suspend fun getWaitingRoom(partyId: String): PartyWaitingRoomDto
    suspend fun readyParty(partyId: String): PartyWaitingRoomDto
    suspend fun leaveParty(partyId: String)
    suspend fun startParty(partyId: String): PartySummaryDto
}

package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import com.example.onuldo_fe.data.party.dto.PartySummaryDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto
import com.example.onuldo_fe.data.party.dto.PartyStartResponseDto
import com.example.onuldo_fe.data.party.dto.PartySettlementResultDto

interface PartyApi {
    suspend fun getParties(): List<PartySummaryDto>
    suspend fun createParty(request: CreatePartyRequestDto): CreatePartyResponseDto
    suspend fun getWaitingRoom(partyId: Long): PartyWaitingRoomDto
    suspend fun readyParty(partyId: Long): PartyWaitingRoomDto
    suspend fun leaveParty(partyId: Long)
    suspend fun startParty(partyId: Long): PartyStartResponseDto
    suspend fun getSettlementResult(partyId: String): PartySettlementResultDto
}

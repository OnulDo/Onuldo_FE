package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import com.example.onuldo_fe.data.party.dto.PartySummaryDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto
import com.example.onuldo_fe.data.party.dummy.FakePartyStore

class FakePartyApi : PartyApi {
    override suspend fun getParties(): List<PartySummaryDto> = FakePartyStore.getInProgressParties()
    override suspend fun createParty(request: CreatePartyRequestDto): CreatePartyResponseDto = FakePartyStore.create(request)
    override suspend fun getWaitingRoom(partyId: String): PartyWaitingRoomDto = FakePartyStore.getRoom(partyId)
    override suspend fun readyParty(partyId: String): PartyWaitingRoomDto = FakePartyStore.ready(partyId)
    override suspend fun leaveParty(partyId: String) = FakePartyStore.leave(partyId)
    override suspend fun startParty(partyId: String): PartySummaryDto = FakePartyStore.start(partyId)
}

package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.model.party.CreatePartyCommand
import com.example.onuldo_fe.model.party.CreatedParty
import com.example.onuldo_fe.model.party.PartySummary
import com.example.onuldo_fe.model.party.PartyWaitingRoom
import com.example.onuldo_fe.model.party.PartySettlementResult

// 파티 생성·목록·대기방·준비·이탈·시작 기능 규칙 정의
interface PartyRepository {
    suspend fun getParties(): List<PartySummary>
    suspend fun createParty(command: CreatePartyCommand): CreatedParty
    suspend fun getWaitingRoom(partyId: String): PartyWaitingRoom
    suspend fun readyParty(partyId: String): PartyWaitingRoom
    suspend fun leaveParty(partyId: String)
    suspend fun startParty(partyId: String)
    suspend fun getSettlementResult(partyId: String): PartySettlementResult
}

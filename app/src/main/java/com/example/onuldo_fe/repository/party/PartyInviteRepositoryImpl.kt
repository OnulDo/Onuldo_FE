package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyInviteApi
import com.example.onuldo_fe.data.party.dto.PartyJoinRequestDto
import com.example.onuldo_fe.data.party.dummy.FakePartyJoinException
import com.example.onuldo_fe.model.party.PartyJoinResult
import java.util.Locale

// 초대코드 참여 요청을 API에 전달하고 참여 결과 반환
class PartyInviteRepositoryImpl(
    private val api: PartyInviteApi
) : PartyInviteRepository {
    override suspend fun joinParty(inviteCode: String): PartyJoinResult = try {
        val room = api.joinParty(PartyJoinRequestDto(inviteCode.trim().uppercase(Locale.ROOT)))
        PartyJoinResult.Success(room.partyId.toString())
    } catch (error: FakePartyJoinException) {
        PartyJoinResult.Failure(error.reason)
    }
}

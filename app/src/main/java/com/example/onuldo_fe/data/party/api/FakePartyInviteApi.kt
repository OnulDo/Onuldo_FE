package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.PartyJoinRequestDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto
import com.example.onuldo_fe.data.party.dummy.FakePartyStore
import com.example.onuldo_fe.data.party.dummy.FakePartyJoinException
import com.example.onuldo_fe.data.party.dummy.PartyInviteDummyData
import com.example.onuldo_fe.model.party.PartyJoinError
import java.util.Locale

class FakePartyInviteApi : PartyInviteApi {
    override suspend fun joinParty(request: PartyJoinRequestDto): PartyWaitingRoomDto {
        val code = request.inviteCode.trim().uppercase(Locale.ROOT)
        when (code) {
            PartyInviteDummyData.STARTED_CODE -> throw FakePartyJoinException(PartyJoinError.AlreadyStarted)
            PartyInviteDummyData.FULL_CODE -> throw FakePartyJoinException(PartyJoinError.Full)
            PartyInviteDummyData.EXPIRED_CODE -> throw FakePartyJoinException(PartyJoinError.Expired)
        }
        return FakePartyStore.join(code)
    }
}

package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dummy.PartyInviteDummyData
import com.example.onuldo_fe.data.party.dummy.FakePartyInviteState
import com.example.onuldo_fe.model.party.PartyJoinError
import com.example.onuldo_fe.model.party.PartyJoinResult

class FakePartyInviteApi : PartyInviteApi {
    override fun joinParty(inviteCode: String): PartyJoinResult {
        val normalizedCode = inviteCode.uppercase()
        return when {
            FakePartyInviteState.isStarted(normalizedCode) -> PartyJoinResult.Failure(PartyJoinError.AlreadyStarted)
            FakePartyInviteState.isExpired(normalizedCode) -> PartyJoinResult.Failure(PartyJoinError.Expired)
            else -> when (normalizedCode) {
            PartyInviteDummyData.VALID_CODE -> PartyJoinResult.Success(PartyInviteDummyData.VALID_PARTY_ID)
            PartyInviteDummyData.STARTED_CODE -> PartyJoinResult.Failure(PartyJoinError.AlreadyStarted)
            PartyInviteDummyData.FULL_CODE -> PartyJoinResult.Failure(PartyJoinError.Full)
            PartyInviteDummyData.EXPIRED_CODE -> PartyJoinResult.Failure(PartyJoinError.Expired)
            else -> PartyJoinResult.Failure(PartyJoinError.Invalid)
            }
        }
    }
}

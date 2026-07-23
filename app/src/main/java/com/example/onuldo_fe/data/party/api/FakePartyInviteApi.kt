package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dummy.PartyInviteDummyData
import com.example.onuldo_fe.data.party.dummy.FakePartyStore
import com.example.onuldo_fe.model.party.PartyJoinError
import com.example.onuldo_fe.model.party.PartyJoinResult

class FakePartyInviteApi : PartyInviteApi {
    override suspend fun joinParty(inviteCode: String): PartyJoinResult {
        val normalizedCode = inviteCode.uppercase()

        // 인원 초과 모달을 파티원 수 설정 없이 바로 확인하기 위한 개발용 테스트 코드입니다.
        if (normalizedCode == PartyInviteDummyData.FULL_CODE) {
            return PartyJoinResult.Failure(PartyJoinError.Full)
        }

        return when (normalizedCode) {
            PartyInviteDummyData.STARTED_CODE -> PartyJoinResult.Failure(PartyJoinError.AlreadyStarted)
            PartyInviteDummyData.EXPIRED_CODE -> PartyJoinResult.Failure(PartyJoinError.Expired)
            else -> FakePartyStore.join(normalizedCode)
        }
    }
}

package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.model.party.PartyJoinError
import org.junit.Assert.assertEquals
import org.junit.Test

class PartyInviteRepositoryImplTest {

    @Test
    fun `서버 오류 코드를 초대코드 오류로 변환한다`() {
        assertEquals(PartyJoinError.Invalid, mapPartyJoinError("INVALID_INVITE_CODE", null))
        assertEquals(PartyJoinError.AlreadyStarted, mapPartyJoinError("PARTY_ALREADY_STARTED", null))
        assertEquals(PartyJoinError.Full, mapPartyJoinError("PARTY_CAPACITY_EXCEEDED", null))
        assertEquals(PartyJoinError.Expired, mapPartyJoinError("INVITE_CODE_EXPIRED", null))
    }

    @Test
    fun `Swagger에 코드가 없으면 서버 메시지로 오류를 변환한다`() {
        assertEquals(PartyJoinError.Invalid, mapPartyJoinError(null, "유효하지 않은 초대코드입니다."))
        assertEquals(PartyJoinError.AlreadyStarted, mapPartyJoinError(null, "이미 시작된 파티입니다."))
        assertEquals(PartyJoinError.Full, mapPartyJoinError(null, "파티 정원이 가득 찼습니다."))
        assertEquals(PartyJoinError.Expired, mapPartyJoinError(null, "만료된 초대코드입니다."))
    }

    @Test
    fun `알 수 없는 서버 오류는 일반 참여 오류로 변환한다`() {
        assertEquals(PartyJoinError.Unknown, mapPartyJoinError("NEW_PARTY_ERROR", "새로운 오류"))
    }
}

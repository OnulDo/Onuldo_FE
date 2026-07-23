package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.model.party.PartyJoinResult

// 초대코드를 이용한 파티 참여 규칙 정의
interface PartyInviteRepository {
    suspend fun joinParty(inviteCode: String): PartyJoinResult
}

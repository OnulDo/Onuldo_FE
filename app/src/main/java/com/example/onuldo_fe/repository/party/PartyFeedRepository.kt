package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.model.party.PartyFeed

// 파티별 인증 피드 조회 규칙 정의
interface PartyFeedRepository {
    suspend fun getPartyFeed(partyId: String): PartyFeed
}

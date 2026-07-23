package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.model.party.PartyChallenge

// 파티에 연계할 챌린지 목록 조회 규칙 정의
interface PartyChallengeRepository {
    suspend fun getPartyChallenges(): List<PartyChallenge>
}

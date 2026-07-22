package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.model.party.PartyChallenge

interface PartyChallengeRepository {
    fun getPartyChallenges(): List<PartyChallenge>
}

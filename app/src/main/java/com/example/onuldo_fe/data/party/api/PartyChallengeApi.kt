package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.PartyChallengeDto

interface PartyChallengeApi {
    fun getPartyChallenges(): List<PartyChallengeDto>
}

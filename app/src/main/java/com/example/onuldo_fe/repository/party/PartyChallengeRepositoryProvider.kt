package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyChallengeApi
import com.example.onuldo_fe.data.party.api.PartyChallengeApi

object PartyChallengeRepositoryProvider {
    fun provide(api: PartyChallengeApi = FakePartyChallengeApi()): PartyChallengeRepository =
        PartyChallengeRepositoryImpl(api)
}

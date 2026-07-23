package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.FakePartyChallengeApi
import com.example.onuldo_fe.data.party.api.PartyChallengeApi

// 챌린지 Repository 구현체와 사용할 API 제공
object PartyChallengeRepositoryProvider {
    fun provide(api: PartyChallengeApi = FakePartyChallengeApi()): PartyChallengeRepository =
        PartyChallengeRepositoryImpl(api)
}

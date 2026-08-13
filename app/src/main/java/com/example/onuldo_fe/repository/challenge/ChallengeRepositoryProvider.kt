package com.example.onuldo_fe.repository.challenge

import com.example.onuldo_fe.data.challenge.api.ChallengeApi
import com.example.onuldo_fe.data.network.RetrofitClient

object ChallengeRepositoryProvider {
    // RetrofitClient(공통 Retrofit 인스턴스)로 ChallengeApi를 생성해 주입한다.
    private val api: ChallengeApi by lazy {
        RetrofitClient.retrofit.create(ChallengeApi::class.java)
    }

    fun provide(): ChallengeRepository = ChallengeRepositoryImpl(api)
}


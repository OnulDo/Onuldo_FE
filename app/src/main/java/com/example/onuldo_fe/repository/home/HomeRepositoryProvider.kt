package com.example.onuldo_fe.repository.home

import com.example.onuldo_fe.data.home.api.RealHomeApi
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.network.NetworkModule
import com.example.onuldo_fe.data.user.api.UserApi

object HomeRepositoryProvider {
    fun provide(
        // 공통 네트워크를 사용해 로그인 토큰을 자동으로 전달한다.
        realApi: RealHomeApi = NetworkModule.create(RealHomeApi::class.java),
        realPartyApi: RealPartyApi = NetworkModule.create(RealPartyApi::class.java),
        // 기존 프로필 API를 재사용해 홈 상단 닉네임을 조회한다.
        userApi: UserApi = NetworkModule.create(UserApi::class.java)
    ): HomeRepository = HomeRepositoryImpl(
        realApi = realApi,
        realPartyApi = realPartyApi,
        userApi = userApi
    )
}

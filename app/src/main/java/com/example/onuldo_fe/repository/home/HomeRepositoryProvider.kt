package com.example.onuldo_fe.repository.home

import com.example.onuldo_fe.data.home.api.FakeHomeApi
import com.example.onuldo_fe.data.home.api.HomeApi
import com.example.onuldo_fe.data.home.api.RealHomeApi
import com.example.onuldo_fe.data.home.config.HomeApiConfig
import com.example.onuldo_fe.data.home.dummy.FakeHomeScenario
import com.example.onuldo_fe.data.network.NetworkModule

object HomeRepositoryProvider {
    private val developmentScenario = FakeHomeScenario.Default

    fun provide(
        fakeApi: HomeApi = FakeHomeApi(developmentScenario),
        // 공통 네트워크를 사용해 로그인 토큰을 자동으로 전달한다.
        realApi: RealHomeApi = NetworkModule.create(RealHomeApi::class.java),
        useRealDailyApi: Boolean = HomeApiConfig.USE_REAL_DAILY
    ): HomeRepository = HomeRepositoryImpl(
        fakeApi = fakeApi,
        realApi = realApi,
        useRealDailyApi = useRealDailyApi
    )
}

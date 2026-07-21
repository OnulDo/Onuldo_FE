package com.example.onuldo_fe.ui.screen.home.data.repository

import com.example.onuldo_fe.ui.screen.home.data.api.FakeHomeApi
import com.example.onuldo_fe.ui.screen.home.data.api.HomeApi

object HomeRepositoryProvider {
    fun provide(homeApi: HomeApi = FakeHomeApi()): HomeRepository {
        // 실제 API 주입 전 기본 Fake API 사용
        return HomeRepositoryImpl(homeApi)
    }
}

package com.example.onuldo_fe.repository.home

import com.example.onuldo_fe.data.home.api.FakeHomeApi
import com.example.onuldo_fe.data.home.api.HomeApi
import com.example.onuldo_fe.data.home.dummy.FakeHomeScenario

object HomeRepositoryProvider {
    // 확인할 홈 화면에 맞춰 하나의 시나리오만 사용 (테스트용)
//    private val developmentScenario = FakeHomeScenario.Default  // 현재 기본 화면
     private val developmentScenario = FakeHomeScenario.Empty // 빈 화면
//     private val developmentScenario = FakeHomeScenario.AllCompleted // 전체 완료 화면

    fun provide(homeApi: HomeApi = FakeHomeApi(developmentScenario)): HomeRepository {
        // 실제 API 주입 전 기본 Fake API 사용
        return HomeRepositoryImpl(homeApi)
    }
}

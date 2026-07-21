package com.example.onuldo_fe.data.home.api

import com.example.onuldo_fe.data.home.dto.HomeResponseDto
import com.example.onuldo_fe.data.home.dummy.FakeHomeScenario
import com.example.onuldo_fe.data.home.dummy.HomeDummyData

class FakeHomeApi(
    private val scenario: FakeHomeScenario = FakeHomeScenario.Default
) : HomeApi {
    override fun getHome(): HomeResponseDto {
        // 선택한 시나리오의 더미 API 응답 반환
        return when (scenario) {
            FakeHomeScenario.Default -> HomeDummyData.withChallenges
            FakeHomeScenario.Empty -> HomeDummyData.empty
            FakeHomeScenario.AllCompleted -> HomeDummyData.allCompleted
        }
    }
}

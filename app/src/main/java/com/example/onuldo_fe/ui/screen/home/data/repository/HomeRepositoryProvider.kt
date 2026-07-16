package com.example.onuldo_fe.ui.screen.home.data.repository

import com.example.onuldo_fe.ui.screen.home.data.api.FakeHomeApi
import com.example.onuldo_fe.ui.screen.home.data.api.HomeApi
import com.example.onuldo_fe.ui.screen.home.data.dto.HomeResponseDto

object HomeRepositoryProvider {
    fun provide(realHomeApi: HomeApi? = null): HomeRepository {
        // realHomeApi가 들어오면 실제 응답 우선 시도, 실패하면 fake로 fallback
        val realResponse = realHomeApi?.runCatching { getHome() }?.getOrNull()

        return if (realResponse.hasHomeData()) {
            HomeRepositoryImpl(requireNotNull(realResponse))
        } else {
            HomeRepositoryImpl(FakeHomeApi().getHome())
        }
    }
}

private fun HomeResponseDto?.hasHomeData(): Boolean {
    // 홈 화면에 표시할 데이터가 하나라도 있으면 real 응답으로 인정
    return this != null &&
        (todayChallenge != null ||
            partyChallenges.isNotEmpty() ||
            challenges.isNotEmpty() ||
            completedChallenges.isNotEmpty())
}

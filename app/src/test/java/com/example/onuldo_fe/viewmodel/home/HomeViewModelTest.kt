package com.example.onuldo_fe.viewmodel.home

import com.example.onuldo_fe.model.home.HomeData
import com.example.onuldo_fe.repository.home.HomeRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeViewModelTest {

    @Test
    fun `늦게 완료된 이전 요청은 최신 홈 상태를 덮어쓰지 않는다`() = runBlocking {
        val firstResponse = CompletableDeferred<HomeData>()
        var requestCount = 0
        val repository = object : HomeRepository {
            override suspend fun getHome(): HomeData {
                requestCount += 1
                return if (requestCount == 1) {
                    // 취소 후에도 응답하는 네트워크 계층을 재현한다.
                    withContext(NonCancellable) { firstResponse.await() }
                } else {
                    HomeData(userName = "최신 응답")
                }
            }
        }
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = HomeViewModel(repository, scope)

        viewModel.loadHome()
        assertEquals("최신 응답", viewModel.uiState.userName)

        firstResponse.complete(HomeData(userName = "이전 응답"))
        assertEquals("최신 응답", viewModel.uiState.userName)
        scope.cancel()
    }
}

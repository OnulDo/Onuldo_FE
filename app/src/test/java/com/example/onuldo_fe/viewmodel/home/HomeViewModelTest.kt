package com.example.onuldo_fe.viewmodel.home

import com.example.onuldo_fe.model.home.HomeData
import com.example.onuldo_fe.repository.home.HomeRepository
import java.io.IOException
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
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class HomeViewModelTest {

    @Test
    fun `홈 오류 메시지는 네트워크와 주요 HTTP 상태를 구분한다`() {
        assertEquals("네트워크 연결을 확인해 주세요.", IOException().toHomeErrorMessage())
        assertEquals("요청을 처리할 수 없어요.", httpException(400).toHomeErrorMessage())
        assertEquals("홈 정보를 찾을 수 없어요.", httpException(404).toHomeErrorMessage())
        assertEquals(
            "서버에 잠시 문제가 생겼어요. 잠시 후 다시 시도해 주세요.",
            httpException(500).toHomeErrorMessage()
        )
    }

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

    private fun httpException(code: Int): HttpException =
        HttpException(Response.error<Unit>(code, "".toResponseBody()))
}

package com.example.onuldo_fe.viewmodel.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.repository.home.HomeRepository
import com.example.onuldo_fe.repository.home.HomeRepositoryProvider
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(
    private val repository: HomeRepository = HomeRepositoryProvider.provide(),
    private val coroutineScope: CoroutineScope? = null
) : ViewModel() {
    private var loadJob: Job? = null
    private var loadGeneration = 0L

    var uiState by mutableStateOf(HomeUiState(isLoading = true))
        private set

    init { loadHome() }

    fun loadHome() {
        requestHome(showFullScreenLoading = !uiState.hasLoadedHome)
    }

    fun refreshHome() {
        requestHome(showFullScreenLoading = false)
    }

    private fun requestHome(showFullScreenLoading: Boolean) {
        val generation = ++loadGeneration
        // 최초 성공 전에는 refresh 요청도 빈 화면을 노출하지 않고 전체 로딩으로 처리한다.
        val shouldShowFullScreenLoading = showFullScreenLoading || !uiState.hasLoadedHome
        // 이전 조회를 취소해 최신 요청만 유지한다.
        loadJob?.cancel()
        // 새로고침은 기존 화면을 유지하고 상단 인디케이터만 표시한다.
        uiState = uiState.copy(
            isLoading = shouldShowFullScreenLoading,
            isRefreshing = !shouldShowFullScreenLoading,
            errorMessage = null
        )
        loadJob = (coroutineScope ?: viewModelScope).launch {
            try {
                // Repository 결과를 화면 상태로 변환한다.
                val loadedState = repository.getHome().toUiState()
                // 취소에 협조하지 않은 이전 요청의 늦은 응답도 무시한다.
                if (generation != loadGeneration) return@launch
                // 정산 결과 조회가 배너를 확인 처리하므로 홈은 서버 응답을 그대로 보여준다.
                uiState = loadedState.copy(hasLoadedHome = true, isRefreshing = false)
            } catch (error: CancellationException) {
                // 코루틴 취소는 오류 화면으로 처리하지 않는다.
                throw error
            } catch (error: Exception) {
                if (generation != loadGeneration) return@launch
                uiState = uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    // 기존 데이터가 있으면 새로고침 실패로 화면 전체를 가리지 않는다.
                    errorMessage = error.toHomeErrorMessage().takeUnless { uiState.hasLoadedHome }
                )
            }
        }
    }
}

internal fun Throwable.toHomeErrorMessage(): String = when (this) {
    is IOException -> "네트워크 연결을 확인해 주세요."
    is HttpException -> when (code()) {
        400 -> "요청을 처리할 수 없어요."
        401, 403 -> "로그인이 만료되었어요. 다시 로그인해 주세요."
        404 -> "홈 정보를 찾을 수 없어요."
        in 500..599 -> "서버에 잠시 문제가 생겼어요. 잠시 후 다시 시도해 주세요."
        else -> "홈 정보를 불러오지 못했어요."
    }
    else -> "홈 정보를 불러오지 못했어요."
}

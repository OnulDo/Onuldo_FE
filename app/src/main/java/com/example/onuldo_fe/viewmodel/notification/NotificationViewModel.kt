package com.example.onuldo_fe.viewmodel.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.repository.notification.NotificationRepository
import com.example.onuldo_fe.repository.notification.NotificationRepositoryProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(NotificationUiState())
        private set

    // 다음 페이지 커서(무한 스크롤·다음 페이지 조회용).
    private var nextCursor: String? = null

    // 진행 중인 첫 페이지 조회. 새로 시작할 때 이전 것을 취소해 늦게 온 옛 응답이 최신 목록을 덮지 않게 한다.
    private var loadJob: Job? = null

    fun loadNotifications() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, loadMoreErrorMessage = null)
            repository.getNotifications()
                .onSuccess { page ->
                    nextCursor = page.nextCursor
                    uiState = uiState.copy(
                        notifications = page.items,
                        hasNext = page.hasNext,
                        isLoading = false,
                    )
                }
                .onError { _, message ->
                    uiState = uiState.copy(isLoading = false, errorMessage = message)
                }
        }
    }

    //목록 끝에 도달했을 때 다음 페이지를 이어 받는다(커서 페이징) - 피드백
    fun loadMore() {
        val cursor = nextCursor
        if (!uiState.hasNext || cursor == null || uiState.isLoading || uiState.isLoadingMore) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true, loadMoreErrorMessage = null)
            repository.getNotifications(cursor)
                .onSuccess { page ->
                    nextCursor = page.nextCursor
                    uiState = uiState.copy(
                        notifications = uiState.notifications + page.items,
                        hasNext = page.hasNext,
                        isLoadingMore = false,
                    )
                }
                .onError { _, message ->
                    // hasNext는 그대로 둔다 — 실패했다고 무한 스크롤 자체를 끝난 것으로 취급하지 않는다.
                    // 화면은 재시도 UI 없이 Toast로만 안내하므로(Route에서 소비), errorMessage와
                    // 분리한 loadMoreErrorMessage에 담아 목록이 있을 때의 새로고침 실패와 구분한다.
                    uiState = uiState.copy(isLoadingMore = false, loadMoreErrorMessage = message)
                }
        }
    }

    /** 다음 페이지 실패 Toast를 화면이 노출한 뒤 호출해 한 번만 뜨도록 비운다. */
    fun onLoadMoreErrorShown() {
        uiState = uiState.copy(loadMoreErrorMessage = null)
    }
}

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
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(NotificationUiState())
        private set

    // 다음 페이지 커서(무한 스크롤·다음 페이지 조회용).
    private var nextCursor: String? = null
    fun loadNotifications() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
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
            uiState = uiState.copy(isLoadingMore = true)
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
                    uiState = uiState.copy(isLoadingMore = false, errorMessage = message)
                }
        }
    }
}

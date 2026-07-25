package com.example.onuldo_fe.viewmodel.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.onuldo_fe.repository.notification.NotificationRepository
import com.example.onuldo_fe.repository.notification.NotificationRepositoryProvider

class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(NotificationUiState())
        private set

    init { loadNotifications() }

    fun loadNotifications() {
        // 알림 응답 전체로 동일 시점의 UI 상태 생성
        uiState = repository.getNotifications().toUiState()
    }
}

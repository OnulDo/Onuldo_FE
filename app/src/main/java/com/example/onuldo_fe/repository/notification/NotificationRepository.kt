package com.example.onuldo_fe.repository.notification

import com.example.onuldo_fe.model.home.notification.NotificationData

interface NotificationRepository {
    // 알림 API 한 번의 응답을 단일 데이터로 전달
    fun getNotifications(): NotificationData
}

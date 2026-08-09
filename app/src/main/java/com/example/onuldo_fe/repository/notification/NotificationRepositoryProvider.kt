package com.example.onuldo_fe.repository.notification

object NotificationRepositoryProvider {
    // API 연동 전 더미 데이터를 반환하는 Repository 사용  // 실제 API 연동 Repository 사용
    fun provide(): NotificationRepository = NotificationRepositoryImpl()
}

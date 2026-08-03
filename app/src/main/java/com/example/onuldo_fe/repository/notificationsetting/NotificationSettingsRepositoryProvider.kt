package com.example.onuldo_fe.repository.notificationsetting

object NotificationSettingsRepositoryProvider {
    // 화면 재구성에도 상태가 유지되도록 단일 인스턴스 사용 (Fake)
    private val repository: NotificationSettingsRepository = NotificationSettingsRepositoryImpl()

    fun provide(): NotificationSettingsRepository = repository
}

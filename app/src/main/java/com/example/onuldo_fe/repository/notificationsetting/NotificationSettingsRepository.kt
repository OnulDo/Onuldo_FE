package com.example.onuldo_fe.repository.notificationsetting

import com.example.onuldo_fe.model.notificationsetting.NotificationSettingType
import com.example.onuldo_fe.ui.screen.mypage.NotificationSettingsState

interface NotificationSettingsRepository {
    // GET /api/users/me/notification-settings — 알림 설정 조회
    fun getSettings(): NotificationSettingsState

    // PATCH /api/users/me/notification-settings — 특정 타입 on/off 변경
    fun updateSetting(type: NotificationSettingType, enabled: Boolean)
}

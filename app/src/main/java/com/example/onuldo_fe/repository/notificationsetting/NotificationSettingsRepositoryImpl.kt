package com.example.onuldo_fe.repository.notificationsetting

import com.example.onuldo_fe.model.notificationsetting.NotificationSettingType
import com.example.onuldo_fe.ui.screen.mypage.NotificationSettingsState

// API 연동 전 in-memory 더미. GET 응답 예시값으로 초기화, PATCH는 로컬 상태만 갱신.
// TODO(API): 실제 GET/PATCH 연동 + DTO(allEnabled/verificationDeadline/verificationResult/
//            challengeStart/refundComplete/deductionAlert) ↔ 앱 필드 매핑
class NotificationSettingsRepositoryImpl : NotificationSettingsRepository {
    private var settings = NotificationSettingsState(
        all = true,            // allEnabled
        challengeStart = true, // challengeStart
        deadline = true,       // verificationDeadline
        result = false,        // verificationResult
        refund = false,        // refundComplete
        deduction = true       // deductionAlert
    )

    override fun getSettings(): NotificationSettingsState = settings

    override fun updateSetting(type: NotificationSettingType, enabled: Boolean) {
        settings = when (type) {
            NotificationSettingType.ChallengeStart -> settings.copy(challengeStart = enabled)
            NotificationSettingType.VerificationDeadline -> settings.copy(deadline = enabled)
            NotificationSettingType.VerificationResult -> settings.copy(result = enabled)
            NotificationSettingType.RefundComplete -> settings.copy(refund = enabled)
            NotificationSettingType.DeductionAlert -> settings.copy(deduction = enabled)
        }
    }
}

package com.example.onuldo_fe.data.user.dto

/** `GET /api/users/me` — 마이 메인 상단 요약. */
data class MyPageResponseDto(
    val nickname: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null,
    val currentPoint: Long = 0L,
    val joinedAt: String? = null,
)

/** `GET /api/users/me/profile` — 프로필 설정 화면. */
data class ProfileResponseDto(
    val nickname: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null,
)

/** 알림 설정 항목. 서버 `UpdateNotificationReqDto.type` enum과 이름이 일치해야 한다. */
enum class NotificationSettingType {
    VERIFICATION_DEADLINE,
    VERIFICATION_RESULT,
    CHALLENGE_START,
    REFUND_COMPLETE,
    DEDUCTION_ALERT,
}

/** `GET /api/users/me/notification-settings` */
data class NotificationSettingsResponseDto(
    val allEnabled: Boolean = false,
    val verificationDeadline: Boolean = false,
    val verificationResult: Boolean = false,
    val challengeStart: Boolean = false,
    val refundComplete: Boolean = false,
    val deductionAlert: Boolean = false,
)

/** `PATCH /api/users/me/notification-settings` — 항목 하나씩 토글한다. */
data class UpdateNotificationRequestDto(
    val type: NotificationSettingType,
    val enabled: Boolean,
)

data class UpdateNotificationResponseDto(
    val type: NotificationSettingType? = null,
    val enabled: Boolean = false,
)

/** `GET /api/users/me/wallet/summary` */
data class WalletSummaryResponseDto(
    val balance: Long = 0L,
    val pendingPoints: Long = 0L,
    val totalDeposit: Long = 0L,
    val totalRefund: Long = 0L,
    val totalPenalty: Long = 0L,
    val averageReturnRate: Int = 0,
)

/** 포인트 거래 종류. 서버 `PointTransactionType`과 일치. */
enum class PointTransactionTypeDto {
    CHARGE,
    WITHDRAW,
    DEPOSIT,
    REFUND,
}

/**
 * `GET /api/users/me/wallet/transactions` 목록 항목.
 *
 * [depositAmount]·[adjustmentAmount]는 도전금과 무관한 거래(충전 등)에서 **실제로 null이 내려온다**(실측).
 * Gson은 Kotlin 기본값을 채워주지 않으므로 nullable로 선언하고 매핑 시점에 0으로 보정한다.
 */
data class PointTransactionResponseDto(
    val type: PointTransactionTypeDto? = null,
    val title: String? = null,
    val amount: Int = 0,
    val depositAmount: Int? = null,
    val adjustmentAmount: Int? = null,
    val balanceAfter: Long = 0L,
    val date: String? = null,
)

/** `POST /api/users/me/wallet/charges` · `.../signup-bonuses` 공통 요청. */
data class ChargePointRequestDto(
    val point: Int,
)

data class ChargePointResponseDto(
    val amount: Int = 0,
    val balanceAfter: Long = 0L,
)

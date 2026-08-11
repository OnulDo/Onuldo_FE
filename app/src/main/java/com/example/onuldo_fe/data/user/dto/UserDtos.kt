package com.example.onuldo_fe.data.user.dto

/** `GET /api/users/me` — 마이 메인 상단 요약. */
data class MyPageResponseDto(
    val nickname: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null,
    val currentPoint: Long = 0L,
    val joinedAt: String? = null,
)

/** `GET /api/users/me/profile` — 프로필 설정 화면. `PATCH` 응답도  동일 - 재사용 */
data class ProfileResponseDto(
    val nickname: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null,
)

/**
 * `PATCH /api/users/me/profile` 요청.
 * 프로필 사진 / 닉네임 요청
 */
data class UpdateProfileRequestDto(
    val nickname: String? = null,
    val profileImageUrl: String? = null,
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

/** `POST /api/users/me/wallet/withdraw` 요청 — 출금할 포인트. */
data class WithdrawPointRequestDto(
    val point: Int,
)

/** `POST /api/users/me/wallet/withdraw` 응답 — 출금 금액과 출금 후 잔액. */
data class WithdrawPointResponseDto(
    val amount: Int = 0,
    val balanceAfter: Long = 0L,
)

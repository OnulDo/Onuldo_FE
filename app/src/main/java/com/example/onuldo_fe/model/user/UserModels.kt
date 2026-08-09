package com.example.onuldo_fe.model.user

import com.example.onuldo_fe.data.user.dto.PointTransactionTypeDto

/** 마이 메인 상단 요약. */
data class MyPageSummary(
    val nickname: String,
    val email: String,
    val profileImageUrl: String?,
    val currentPoint: Long,
    val joinedAt: String?,
)

/** 프로필 설정 화면용 사용자 정보. */
data class UserProfile(
    val nickname: String,
    val email: String,
    val profileImageUrl: String?,
)

// NotificationSettings 모델은 model/notification 로 이동함.

/** 포인트 지갑 요약. */
data class WalletSummary(
    val balance: Long,
    val pendingPoints: Long,
    val totalDeposit: Long,
    val totalRefund: Long,
    val totalPenalty: Long,
    val averageReturnRate: Int,
) {
    companion object {
        val EMPTY = WalletSummary(0L, 0L, 0L, 0L, 0L, 0)
    }
}

/** 포인트 거래 내역 한 건. */
data class PointTransaction(
    val type: PointTransactionTypeDto?,
    val title: String,
    val amount: Int,
    val depositAmount: Int,
    val adjustmentAmount: Int,
    val balanceAfter: Long,
    val date: String,
)

/** 포인트 충전·보너스 지급 결과. */
data class PointChargeResult(
    val amount: Int,
    val balanceAfter: Long,
)

/** 포인트 출금 결과 — 출금 금액과 출금 후 잔액 */
data class PointWithdrawResult(
    val amount: Int,
    val balanceAfter: Long,
)

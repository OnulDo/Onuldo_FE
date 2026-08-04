package com.example.onuldo_fe.model.user

import com.example.onuldo_fe.data.user.dto.NotificationSettingType
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

/** 알림 설정 상태. [allEnabled]는 서버가 계산해 주는 전체 토글 값이다. */
data class NotificationSettings(
    val allEnabled: Boolean,
    val verificationDeadline: Boolean,
    val verificationResult: Boolean,
    val challengeStart: Boolean,
    val refundComplete: Boolean,
    val deductionAlert: Boolean,
) {
    /** 항목 종류로 현재 값을 읽는다. */
    fun valueOf(type: NotificationSettingType): Boolean = when (type) {
        NotificationSettingType.VERIFICATION_DEADLINE -> verificationDeadline
        NotificationSettingType.VERIFICATION_RESULT -> verificationResult
        NotificationSettingType.CHALLENGE_START -> challengeStart
        NotificationSettingType.REFUND_COMPLETE -> refundComplete
        NotificationSettingType.DEDUCTION_ALERT -> deductionAlert
    }

    /** 항목 하나를 바꾼 새 상태. 전체 토글은 개별 값에서 다시 계산한다. */
    fun withValue(type: NotificationSettingType, enabled: Boolean): NotificationSettings {
        val updated = when (type) {
            NotificationSettingType.VERIFICATION_DEADLINE -> copy(verificationDeadline = enabled)
            NotificationSettingType.VERIFICATION_RESULT -> copy(verificationResult = enabled)
            NotificationSettingType.CHALLENGE_START -> copy(challengeStart = enabled)
            NotificationSettingType.REFUND_COMPLETE -> copy(refundComplete = enabled)
            NotificationSettingType.DEDUCTION_ALERT -> copy(deductionAlert = enabled)
        }
        return updated.copy(
            allEnabled = with(updated) {
                verificationDeadline && verificationResult && challengeStart &&
                    refundComplete && deductionAlert
            }
        )
    }

    companion object {
        /** 서버 응답 전 표시할 기본값(모두 꺼짐). */
        val EMPTY = NotificationSettings(
            allEnabled = false,
            verificationDeadline = false,
            verificationResult = false,
            challengeStart = false,
            refundComplete = false,
            deductionAlert = false,
        )
    }
}

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

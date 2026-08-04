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

/**
 * 알림 설정 상태.
 *
 * [allEnabled]는 개별 5종과 **독립적인 마스터 스위치**다(서버도 `all_enabled` 별도 컬럼으로 저장).
 * 화면에서 이 값이 꺼지면 개별 토글이 비활성화되므로, 개별 항목을 끈다고 해서 이 값을 함께
 * 내리면 안 된다(그러면 하나만 꺼도 나머지가 전부 잠긴다).
 *
 * ⚠️ 서버 한계: `PATCH /notification-settings`의 type enum에 `ALL`이 없고
 * `NotificationSetting.apply()`도 `all_enabled`를 건드리지 않아, **마스터 스위치를 끈 상태가
 * 서버에 저장되지 않는다**(항상 기본값 `true`로 조회됨). 앱은 마스터를 끌 때 개별 5종을 모두
 * 꺼서 실제 알림 수신은 차단하지만, 화면 재진입 시 마스터는 다시 켜진 것으로 보인다.
 * 백엔드에 `ALL` 타입 추가 요청 필요.
 */
data class NotificationSettings(
    val allEnabled: Boolean,
    val verificationDeadline: Boolean,
    val verificationResult: Boolean,
    val challengeStart: Boolean,
    val refundComplete: Boolean,
    val deductionAlert: Boolean,
) {
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

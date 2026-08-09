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
 * `NotificationSetting.apply()`도 `all_enabled`를 건드리지 않아, **마스터 스위치 자체는
 * 저장할 수 없다**(항상 기본값 `true`로 조회됨). [allEnabled]는 조회 응답을 그대로 담을 뿐이므로
 * 화면 표시에 쓰지 말 것 — 화면은 개별 5종에서 마스터 상태를 파생한다
 * (`NotificationSettingsViewModel`의 변환 참고).
 *
 * 백엔드에 `ALL` 타입이 추가되면 이 값을 그대로 쓰도록 되돌릴 수 있다.
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
    // 도전금·조정액은 도전 관련 거래에만 존재하고 충전·출금 등에선 서버가 null로 준다.
    // "값 없음"과 "실제 0"을 구분하려고 nullable 원본을 그대로 둔다(0으로 채우지 않음).
    // breakdown 표시는 둘 다 non-null일 때만 — 하나라도 null이면 표시하지 않는다.
    val depositAmount: Int?,
    val adjustmentAmount: Int?,
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

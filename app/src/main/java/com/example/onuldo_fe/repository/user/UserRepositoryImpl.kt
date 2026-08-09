package com.example.onuldo_fe.repository.user

import com.example.onuldo_fe.data.network.ApiResult
import com.example.onuldo_fe.data.network.CursorPage
import com.example.onuldo_fe.data.network.map
import com.example.onuldo_fe.data.network.safeApiCall
import com.example.onuldo_fe.data.network.safeCursorApiCall
import com.example.onuldo_fe.data.network.safeUnitApiCall
import com.example.onuldo_fe.data.user.api.UserApi
import com.example.onuldo_fe.data.user.dto.ChargePointRequestDto
import com.example.onuldo_fe.data.notification.api.NotificationApi
import com.example.onuldo_fe.data.notification.dto.NotificationSettingType
import com.example.onuldo_fe.data.user.dto.PointTransactionTypeDto
import com.example.onuldo_fe.data.notification.dto.UpdateNotificationRequestDto
import com.example.onuldo_fe.data.user.dto.UpdateProfileRequestDto
import com.example.onuldo_fe.data.user.dto.WithdrawPointRequestDto
import com.example.onuldo_fe.model.user.MyPageSummary
import com.example.onuldo_fe.model.notification.NotificationSettings
import com.example.onuldo_fe.model.user.PointChargeResult
import com.example.onuldo_fe.model.user.PointTransaction
import com.example.onuldo_fe.model.user.PointWithdrawResult
import com.example.onuldo_fe.model.user.UserProfile
import com.example.onuldo_fe.model.user.WalletSummary

class UserRepositoryImpl(
    private val userApi: UserApi,
    // 알림 설정 API는 notification 패키지로 이동해 NotificationApi로 호출한다.
    private val notificationApi: NotificationApi,
) : UserRepository {

    override suspend fun getMyPage(): ApiResult<MyPageSummary> =
        safeApiCall { userApi.getMyPage() }.map { dto ->
            MyPageSummary(
                nickname = dto.nickname.orEmpty(),
                email = dto.email.orEmpty(),
                profileImageUrl = dto.profileImageUrl,
                currentPoint = dto.currentPoint,
                joinedAt = dto.joinedAt,
            )
        }

    override suspend fun getProfile(): ApiResult<UserProfile> =
        safeApiCall { userApi.getProfile() }.map { dto ->
            UserProfile(
                nickname = dto.nickname.orEmpty(),
                email = dto.email.orEmpty(),
                profileImageUrl = dto.profileImageUrl,
            )
        }

    override suspend fun updateProfile(
        nickname: String?,
        profileImageUrl: String?,
    ): ApiResult<UserProfile> =
        safeApiCall {
            userApi.updateProfile(UpdateProfileRequestDto(nickname, profileImageUrl))
        }.map { dto ->
            // PATCH 응답에는 email이 없을 수 있어 도메인 모델에서는 빈 문자열로 처리
            UserProfile(
                nickname = dto.nickname.orEmpty(),
                email = dto.email.orEmpty(),
                profileImageUrl = dto.profileImageUrl,
            )
        }

    override suspend fun deleteAccount(): ApiResult<Unit> =
        safeUnitApiCall { userApi.deleteAccount() }

    override suspend fun getNotificationSettings(): ApiResult<NotificationSettings> =
        safeApiCall { notificationApi.getNotificationSettings() }.map { dto ->
            NotificationSettings(
                allEnabled = dto.allEnabled,
                verificationDeadline = dto.verificationDeadline,
                challengeStart = dto.challengeStart,
                challengeEndReminder = dto.challengeEndReminder,
                verificationResult = dto.verificationResult,
                partyMemberVerified = dto.partyMemberVerified,
                settlementComplete = dto.settlementComplete,
            )
        }

    override suspend fun updateNotificationSetting(
        type: NotificationSettingType,
        enabled: Boolean,
    ): ApiResult<Unit> =
        safeApiCall { notificationApi.updateNotificationSetting(UpdateNotificationRequestDto(type, enabled)) }
            .map { }

    override suspend fun getWalletSummary(): ApiResult<WalletSummary> =
        safeApiCall { userApi.getWalletSummary() }.map { dto ->
            WalletSummary(
                balance = dto.balance,
                pendingPoints = dto.pendingPoints,
                totalDeposit = dto.totalDeposit,
                totalRefund = dto.totalRefund,
                totalPenalty = dto.totalPenalty,
                averageReturnRate = dto.averageReturnRate,
            )
        }

    override suspend fun getWalletTransactions(
        type: PointTransactionTypeDto?,
        cursor: String?,
        size: Int,
    ): ApiResult<CursorPage<PointTransaction>> =
        safeCursorApiCall {
            userApi.getWalletTransactions(type = type?.name, cursor = cursor, size = size)
        }.map { page ->
            CursorPage(
                items = page.items.map { dto ->
                    PointTransaction(
                        type = dto.type,
                        title = dto.title.orEmpty(),
                        amount = dto.amount,
                        // null은 그대로 둔다(0으로 채우면 "값 없음"과 "실제 0"이 섞인다).
                        depositAmount = dto.depositAmount,
                        adjustmentAmount = dto.adjustmentAmount,
                        balanceAfter = dto.balanceAfter,
                        date = dto.date.orEmpty(),
                    )
                },
                nextCursor = page.nextCursor,
                hasNext = page.hasNext,
            )
        }

    override suspend fun chargePoint(point: Int): ApiResult<PointChargeResult> =
        safeApiCall { userApi.chargePoint(ChargePointRequestDto(point)) }.map { dto ->
            PointChargeResult(amount = dto.amount, balanceAfter = dto.balanceAfter)
        }

    override suspend fun withdrawPoint(point: Int): ApiResult<PointWithdrawResult> =
        safeApiCall { userApi.withdrawPoint(WithdrawPointRequestDto(point)) }.map { dto ->
            PointWithdrawResult(amount = dto.amount, balanceAfter = dto.balanceAfter)
        }

    override suspend fun grantSignupBonus(point: Int): ApiResult<PointChargeResult> =
        safeApiCall { userApi.grantSignupBonus(ChargePointRequestDto(point)) }.map { dto ->
            PointChargeResult(amount = dto.amount, balanceAfter = dto.balanceAfter)
        }
}

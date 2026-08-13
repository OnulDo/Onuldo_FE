package com.example.onuldo_fe.viewmodel.mypage

import com.example.onuldo_fe.data.network.ApiResult
import com.example.onuldo_fe.data.network.CursorPage
import com.example.onuldo_fe.data.notification.dto.NotificationSettingType
import com.example.onuldo_fe.data.user.dto.PointTransactionTypeDto
import com.example.onuldo_fe.model.notification.NotificationSettings
import com.example.onuldo_fe.model.user.MyPageSummary
import com.example.onuldo_fe.model.user.PointChargeResult
import com.example.onuldo_fe.model.user.PointTransaction
import com.example.onuldo_fe.model.user.PointWithdrawResult
import com.example.onuldo_fe.model.user.UserProfile
import com.example.onuldo_fe.model.user.WalletSummary
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.ui.screen.mypage.NotificationSettingsState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * 알림 설정 변경이 서버 정책과 일치하는지 확인한다.
 *
 * 전체 알림 토글은 ALL_ENABLED 한 건으로 PATCH하고,
 * 개별 알림은 해당 타입만 PATCH한다.
 */
class NotificationSettingsViewModelTest {

    @Test
    fun `전체 토글 도중 실패하면 남은 요청을 보내지 않는다`() = runBlocking {
        val repository = FakeUserRepository(failAtAttempt = 1)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = NotificationSettingsViewModel(repository, scope)

        viewModel.apply(ALL_OFF)

        assertEquals(1, repository.attempts)
        assertEquals(0, repository.applied.size)

        scope.cancel()
    }

    @Test
    fun `전체 토글 실패하면 화면을 서버 값에 다시 맞춘다`() = runBlocking {
        val repository = FakeUserRepository(failAtAttempt = 1)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = NotificationSettingsViewModel(repository, scope)

        viewModel.apply(ALL_OFF)

        val server = repository.serverSettings
        val shown = viewModel.state.value

        assertEquals(server.verificationDeadline, shown.deadline)
        assertEquals(server.challengeStart, shown.challengeStart)
        assertEquals(server.challengeEndReminder, shown.endReminder)
        assertEquals(server.verificationResult, shown.result)
        assertEquals(server.partyMemberVerified, shown.partyMemberVerified)
        assertEquals(server.settlementComplete, shown.refund)
        assertEquals(server.allEnabled, shown.all)

        assertEquals(2, repository.loadCount)
        assertNotNull(viewModel.errorMessage.value)

        scope.cancel()
    }

    @Test
    fun `전체 토글이 성공하면 ALL_ENABLED 한 건만 요청한다`() = runBlocking {
        val repository = FakeUserRepository(failAtAttempt = Int.MAX_VALUE)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = NotificationSettingsViewModel(repository, scope)

        viewModel.apply(ALL_OFF)

        assertEquals(
            listOf(NotificationSettingType.ALL_ENABLED to false),
            repository.applied,
        )
        assertEquals(1, repository.loadCount)
        assertEquals(ALL_OFF, viewModel.state.value)

        scope.cancel()
    }

    @Test
    fun `같은 항목을 빠르게 두 번 토글하면 마지막에 누른 값이 서버에 남는다`() = runBlocking {
        val firstPatch = CompletableDeferred<Unit>()
        val repository = FakeUserRepository(
            failAtAttempt = Int.MAX_VALUE,
            holdFirstPatch = firstPatch,
        )
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = NotificationSettingsViewModel(repository, scope)

        val base = viewModel.state.value

        viewModel.apply(base.copy(deadline = false))
        viewModel.apply(base.copy(deadline = true))

        firstPatch.complete(Unit)

        assertEquals(true, repository.serverSettings.verificationDeadline)
        assertEquals(true, viewModel.state.value.deadline)
        assertEquals(
            listOf(
                NotificationSettingType.VERIFICATION_DEADLINE to false,
                NotificationSettingType.VERIFICATION_DEADLINE to true,
            ),
            repository.applied,
        )

        scope.cancel()
    }

    @Test
    fun `재조회 도중 누른 토글을 옛 서버 값이 덮어쓰지 않는다`() = runBlocking {
        val holdRefresh = CompletableDeferred<Unit>()
        val repository = FakeUserRepository(
            failAtAttempt = Int.MAX_VALUE,
            failOnlyAtAttempt = 1,
            holdRefresh = holdRefresh,
        )
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = NotificationSettingsViewModel(repository, scope)

        viewModel.apply(ALL_OFF)

        val newest = ALL_OFF.copy(challengeStart = true)
        viewModel.apply(newest)

        holdRefresh.complete(Unit)

        assertEquals(newest, viewModel.state.value)

        scope.cancel()
    }

    private companion object {
        /** 전체 알림을 끈 상태. 필수 알림인 정산/환급 완료는 서버 정책에 따라 유지된다. */
        val ALL_OFF = NotificationSettingsState(
            all = false,
            challengeStart = false,
            deadline = false,
            endReminder = false,
            result = false,
            partyMemberVerified = false,
            refund = true,
        )
    }
}

/**
 * 알림 설정만 실제로 동작하는 테스트용 가짜 저장소.
 *
 * 실제 서버 대신 메모리의 serverSettings를 사용해 GET/PATCH 동작을 검증한다.
 * 알림 설정 외 메서드가 호출되면 즉시 실패한다.
 */
private class FakeUserRepository(
    private val failAtAttempt: Int,
    private val holdFirstPatch: CompletableDeferred<Unit>? = null,
    private val failOnlyAtAttempt: Int? = null,
    private val holdRefresh: CompletableDeferred<Unit>? = null,
) : UserRepository {

    var serverSettings = NotificationSettings(
        allEnabled = true,
        verificationDeadline = true,
        challengeStart = true,
        challengeEndReminder = true,
        verificationResult = true,
        partyMemberVerified = true,
        settlementComplete = true,
    )
        private set

    val applied = mutableListOf<Pair<NotificationSettingType, Boolean>>()
    var attempts = 0
        private set
    var loadCount = 0
        private set

    override suspend fun getNotificationSettings(): ApiResult<NotificationSettings> {
        loadCount += 1

        if (loadCount == 2) {
            holdRefresh?.await()
        }

        return ApiResult.Success(serverSettings)
    }

    override suspend fun updateNotificationSetting(
        type: NotificationSettingType,
        enabled: Boolean,
    ): ApiResult<Unit> {
        attempts += 1

        if (attempts == 1) {
            holdFirstPatch?.await()
        }

        val shouldFail =
            if (failOnlyAtAttempt != null) {
                attempts == failOnlyAtAttempt
            } else {
                attempts >= failAtAttempt
            }

        if (shouldFail) {
            return ApiResult.Failure(
                code = "INTERNAL_SERVER_ERROR",
                message = "서버 오류",
            )
        }

        applied += type to enabled
        serverSettings = serverSettings.with(type, enabled)

        return ApiResult.Success(Unit)
    }

    private fun NotificationSettings.with(
        type: NotificationSettingType,
        enabled: Boolean,
    ): NotificationSettings =
        when (type) {
            NotificationSettingType.ALL_ENABLED ->
                if (enabled) {
                    copy(
                        allEnabled = true,
                        verificationDeadline = true,
                        challengeStart = true,
                        challengeEndReminder = true,
                        verificationResult = true,
                        partyMemberVerified = true,
                        settlementComplete = true,
                    )
                } else {
                    copy(
                        allEnabled = false,
                        verificationDeadline = false,
                        challengeStart = false,
                        challengeEndReminder = false,
                        verificationResult = false,
                        partyMemberVerified = false,
                        // 필수 알림은 유지
                    )
                }

            NotificationSettingType.VERIFICATION_DEADLINE ->
                copy(verificationDeadline = enabled)

            NotificationSettingType.CHALLENGE_START ->
                copy(challengeStart = enabled)

            NotificationSettingType.CHALLENGE_END_REMINDER ->
                copy(challengeEndReminder = enabled)

            NotificationSettingType.VERIFICATION_RESULT ->
                copy(verificationResult = enabled)

            NotificationSettingType.PARTY_MEMBER_VERIFIED ->
                copy(partyMemberVerified = enabled)

            NotificationSettingType.SETTLEMENT_COMPLETE ->
                copy(settlementComplete = enabled)
        }

    private fun unused(member: String): Nothing =
        error("이 테스트에서 사용하지 않음: $member")

    override suspend fun getMyPage(): ApiResult<MyPageSummary> =
        unused("getMyPage")

    override suspend fun getProfile(): ApiResult<UserProfile> =
        unused("getProfile")

    override suspend fun updateProfile(
        nickname: String?,
        profileImageUrl: String?,
    ): ApiResult<UserProfile> =
        unused("updateProfile")

    override suspend fun deleteAccount(): ApiResult<Unit> =
        unused("deleteAccount")

    override suspend fun getWalletSummary(): ApiResult<WalletSummary> =
        unused("getWalletSummary")

    override suspend fun getWalletTransactions(
        type: PointTransactionTypeDto?,
        cursor: String?,
        size: Int,
    ): ApiResult<CursorPage<PointTransaction>> =
        unused("getWalletTransactions")

    override suspend fun chargePoint(point: Int): ApiResult<PointChargeResult> =
        unused("chargePoint")

    override suspend fun withdrawPoint(point: Int): ApiResult<PointWithdrawResult> =
        unused("withdrawPoint")

    override suspend fun grantSignupBonus(point: Int): ApiResult<PointChargeResult> =
        unused("grantSignupBonus")
}
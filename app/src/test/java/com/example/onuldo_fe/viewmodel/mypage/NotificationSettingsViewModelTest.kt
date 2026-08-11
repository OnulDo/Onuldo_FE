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
 * 전체 알림 토글은 개별 6종을 하나씩 PATCH한다. 그 도중 하나가 실패했을 때
 * 화면과 서버가 어긋나지 않는지 확인한다.
 *
 * 수동으로는 "6건 중 3번째만 실패"를 만들 수 없어 테스트로 고정한다.
 */
class NotificationSettingsViewModelTest {

    @Test
    fun `전체 토글 도중 실패하면 남은 항목을 보내지 않는다`() = runBlocking {
        val repository = FakeUserRepository(failAtAttempt = 3)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = NotificationSettingsViewModel(repository, scope)

        viewModel.apply(ALL_OFF)

        // 3번째에서 멈춰야 한다. 계속 보내면 화면은 실패로 처리했는데
        // 나머지 항목만 새 값으로 저장돼 서버와 어긋난다.
        assertEquals(3, repository.attempts)
        assertEquals(2, repository.applied.size)

        scope.cancel()
    }

    @Test
    fun `전체 토글 도중 실패하면 화면을 서버 값에 다시 맞춘다`() = runBlocking {
        val repository = FakeUserRepository(failAtAttempt = 3)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = NotificationSettingsViewModel(repository, scope)

        viewModel.apply(ALL_OFF)

        // 실패 이전 2건은 서버에 이미 저장됐다. 화면을 통째로 되돌리면 그것대로 어긋나므로
        // 서버를 다시 읽어 맞춘 상태여야 한다.
        val server = repository.serverSettings
        val shown = viewModel.state.value

        assertEquals(server.verificationDeadline, shown.deadline)
        assertEquals(server.challengeStart, shown.challengeStart)
        assertEquals(server.challengeEndReminder, shown.endReminder)
        assertEquals(server.verificationResult, shown.result)
        assertEquals(server.partyMemberVerified, shown.partyMemberVerified)
        assertEquals(server.settlementComplete, shown.refund)
        assertEquals(server.allEnabled, shown.all)

        // 최초 로드 + 실패 후 재조회
        assertEquals(2, repository.loadCount)
        assertNotNull(viewModel.errorMessage.value)

        scope.cancel()
    }

    @Test
    fun `모두 성공하면 재조회하지 않는다`() = runBlocking {
        val repository = FakeUserRepository(failAtAttempt = Int.MAX_VALUE)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = NotificationSettingsViewModel(repository, scope)

        viewModel.apply(ALL_OFF)

        assertEquals(6, repository.applied.size)
        assertEquals(1, repository.loadCount) // 최초 로드뿐
        assertEquals(ALL_OFF, viewModel.state.value)

        scope.cancel()
    }

    @Test
    fun `같은 항목을 빠르게 두 번 토글하면 마지막에 누른 값이 서버에 남는다`() = runBlocking {
        val firstPatch = CompletableDeferred<Unit>()
        val repository = FakeUserRepository(failAtAttempt = Int.MAX_VALUE, holdFirstPatch = firstPatch)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = NotificationSettingsViewModel(repository, scope)

        val base = viewModel.state.value

        // 끄기 → (응답 전에) 켜기. 두 요청이 겹친다.
        viewModel.apply(base.copy(deadline = false))
        viewModel.apply(base.copy(deadline = true))

        // 첫 요청이 뒤늦게 완료된다.
        firstPatch.complete(Unit)

        // 직렬화되지 않으면 나중에 도착한 "끄기"가 마지막에 저장돼 화면(켜짐)과 갈린다.
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
            // 첫 PATCH만 실패시켜 재조회로 들어가게 한다.
            failOnlyAtAttempt = 1,
            holdRefresh = holdRefresh,
        )
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val viewModel = NotificationSettingsViewModel(repository, scope)

        // 저장 실패 → 서버 재조회 시작(응답 대기 상태로 멈춘다).
        viewModel.apply(ALL_OFF)

        // 응답을 기다리는 사이 사용자가 다른 토글을 누른다.
        val newest = ALL_OFF.copy(challengeStart = true)
        viewModel.apply(newest)

        // 이제 재조회 응답이 도착한다. 서버에는 첫 PATCH가 실패해 옛 값(전부 켜짐)이 남아 있다.
        holdRefresh.complete(Unit)

        // 반영 시점에 세대를 확인하지 않으면 옛 서버 값이 방금 누른 값을 덮어쓴다.
        assertEquals(newest, viewModel.state.value)

        scope.cancel()
    }

    private companion object {
        /** 화면의 "전체 알림 수신"을 끈 상태 — 개별 6종도 함께 꺼진다. */
        val ALL_OFF = NotificationSettingsState(
            all = false,
            challengeStart = false,
            deadline = false,
            endReminder = false,
            result = false,
            partyMemberVerified = false,
            refund = false,
        )
    }
}

/**
 * 알림 설정만 실제로 동작하는 가짜 저장소.
 * [failAtAttempt]번째 PATCH부터 실패시키고, 성공한 것만 [serverSettings]에 반영한다.
 */
private class FakeUserRepository(
    private val failAtAttempt: Int,
    /** 지정하면 첫 PATCH가 이 신호를 기다린다. 요청이 겹치는 상황을 만들 때 쓴다. */
    private val holdFirstPatch: CompletableDeferred<Unit>? = null,
    /** 지정하면 [failAtAttempt] 대신 이 순번의 PATCH **하나만** 실패시킨다. */
    private val failOnlyAtAttempt: Int? = null,
    /** 지정하면 두 번째 GET(저장 실패 후 재조회)이 이 신호를 기다린다. */
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
        // 1번째는 init의 최초 로드, 2번째가 저장 실패 후 재조회다.
        if (loadCount == 2) holdRefresh?.await()
        return ApiResult.Success(serverSettings)
    }

    override suspend fun updateNotificationSetting(
        type: NotificationSettingType,
        enabled: Boolean,
    ): ApiResult<Unit> {
        attempts += 1
        if (attempts == 1) holdFirstPatch?.await()

        val shouldFail =
            if (failOnlyAtAttempt != null) attempts == failOnlyAtAttempt else attempts >= failAtAttempt
        if (shouldFail) {
            return ApiResult.Failure(code = "INTERNAL_SERVER_ERROR", message = "서버 오류")
        }
        applied += type to enabled
        serverSettings = serverSettings.with(type, enabled)
        return ApiResult.Success(Unit)
    }

    private fun NotificationSettings.with(
        type: NotificationSettingType,
        enabled: Boolean,
    ): NotificationSettings = when (type) {
        NotificationSettingType.VERIFICATION_DEADLINE -> copy(verificationDeadline = enabled)
        NotificationSettingType.CHALLENGE_START -> copy(challengeStart = enabled)
        NotificationSettingType.CHALLENGE_END_REMINDER -> copy(challengeEndReminder = enabled)
        NotificationSettingType.VERIFICATION_RESULT -> copy(verificationResult = enabled)
        NotificationSettingType.PARTY_MEMBER_VERIFIED -> copy(partyMemberVerified = enabled)
        NotificationSettingType.SETTLEMENT_COMPLETE -> copy(settlementComplete = enabled)
    }

    // --- 이 테스트에서 쓰지 않는 것들 ---
    override suspend fun getMyPage(): ApiResult<MyPageSummary> = TODO("사용하지 않음")
    override suspend fun getProfile(): ApiResult<UserProfile> = TODO("사용하지 않음")
    override suspend fun updateProfile(
        nickname: String?,
        profileImageUrl: String?,
    ): ApiResult<UserProfile> = TODO("사용하지 않음")

    override suspend fun deleteAccount(): ApiResult<Unit> = TODO("사용하지 않음")
    override suspend fun getWalletSummary(): ApiResult<WalletSummary> = TODO("사용하지 않음")
    override suspend fun getWalletTransactions(
        type: PointTransactionTypeDto?,
        cursor: String?,
        size: Int,
    ): ApiResult<CursorPage<PointTransaction>> = TODO("사용하지 않음")

    override suspend fun chargePoint(point: Int): ApiResult<PointChargeResult> = TODO("사용하지 않음")
    override suspend fun withdrawPoint(point: Int): ApiResult<PointWithdrawResult> =
        TODO("사용하지 않음")

    override suspend fun grantSignupBonus(point: Int): ApiResult<PointChargeResult> =
        TODO("사용하지 않음")
}

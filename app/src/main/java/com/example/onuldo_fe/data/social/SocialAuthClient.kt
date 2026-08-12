package com.example.onuldo_fe.data.social

import android.content.Context
import android.util.Log
import com.example.onuldo_fe.BuildConfig
import com.example.onuldo_fe.data.auth.dto.SocialProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/** 소셜 SDK 로그인 결과. 서버 `oauth/login`에 넘길 [accessToken]을 얻는 것이 목적이다. */
sealed interface SocialAuthResult {
    data class Success(val accessToken: String) : SocialAuthResult

    /** 사용자가 로그인 창을 닫은 경우. 에러 문구를 띄우지 않는다. */
    data object Cancelled : SocialAuthResult

    data class Failure(val message: String) : SocialAuthResult
}

/**
 * 카카오 SDK 래퍼.
 *
 * SDK가 콜백 기반이라 [suspendCancellableCoroutine]으로 감싸 ViewModel에서 코루틴으로 쓴다.
 * 여기서 얻은 `accessToken`을 서버 `POST /api/auth/oauth/login`에 넘기면 우리 서비스의 JWT가 발급된다.
 *
 * 키는 `local.properties` → `BuildConfig`로 주입된다. 키가 비어 있으면 [isConfigured]가 false가 되고
 * 화면은 소셜 로그인 버튼을 비활성화한다.
 *
 * > 네이버 로그인은 제거됐다(2026-08-11). 네이버 검수에서 서비스 URL·소개 자료 미비로 반려됐고,
 * > 팀 논의 결과 카카오만 유지하기로 확정했다. Figma Ready for Dev 로그인 화면(`4771:384`)도
 * > 카카오 버튼 하나만 남은 디자인이다.
 */
object SocialAuthClient : SocialAccountLink {

    private var initialized = false

    val isKakaoConfigured: Boolean get() = BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank()

    fun isConfigured(provider: SocialProvider): Boolean = when (provider) {
        SocialProvider.KAKAO -> isKakaoConfigured
        SocialProvider.EMAIL -> false
    }

    /** [OnuldoApplication]에서 앱 시작 시 1회 호출. */
    fun initialize(context: Context) {
        if (initialized) return
        initialized = true

        SocialSessionStore.initialize(context)

        if (isKakaoConfigured) {
            KakaoSdk.init(context, BuildConfig.KAKAO_NATIVE_APP_KEY)
        }
    }

    /**
     * 소셜 로그인.
     *
     * [forceAccountSelection]이 true면 **기존 세션을 재사용하지 않고 계정을 다시 고르게** 한다.
     * 소셜 SDK는 기본적으로 기기에 남은 세션으로 조용히 로그인해서, 한 번 로그인한 뒤에는
     * 계정 선택 화면이 다시 뜨지 않는다. 다른 계정으로 갈아타려면 이 값이 필요하다.
     */
    suspend fun login(
        context: Context,
        provider: SocialProvider,
        forceAccountSelection: Boolean = false,
    ): SocialAuthResult =
        when (provider) {
            SocialProvider.KAKAO -> loginWithKakao(context, forceAccountSelection)
            SocialProvider.EMAIL -> SocialAuthResult.Failure("지원하지 않는 로그인 방식이에요.")
        }

    /**
     * 카카오 로그인.
     *
     * 카카오톡 앱이 설치돼 있으면 앱으로, 없으면 웹(카카오계정)으로 로그인한다.
     * 카카오톡 로그인 중 사용자가 취소하면 웹으로 넘어가지 않고 그대로 취소 처리한다
     * (그렇지 않으면 취소했는데 웹 로그인 창이 다시 뜬다).
     *
     * **[forceAccountSelection]이면 카카오톡 경로를 건너뛴다.** 카카오톡 로그인은 그 앱에 로그인된
     * 계정에 묶여 있어 계정 선택이라는 개념이 없다(`loginWithKakaoTalk`에는 `prompts` 파라미터가
     * 아예 없다). 계정을 고르게 하려면 웹 경로에서 [Prompt.SELECT_ACCOUNT]를 줘야 한다.
     */
    private suspend fun loginWithKakao(
        context: Context,
        forceAccountSelection: Boolean,
    ): SocialAuthResult {
        if (!isKakaoConfigured) return SocialAuthResult.Failure(NOT_CONFIGURED_MESSAGE)

        if (forceAccountSelection) return kakaoAccountLogin(context, selectAccount = true)

        val useKakaoTalk = UserApiClient.instance.isKakaoTalkLoginAvailable(context)
        if (!useKakaoTalk) return kakaoAccountLogin(context)

        return when (val result = kakaoTalkLogin(context)) {
            is SocialAuthResult.Failure ->
                // 카카오톡에 로그인된 계정이 없는 등의 이유면 웹 로그인으로 대체한다.
                if (result.message == FALLBACK_TO_ACCOUNT) kakaoAccountLogin(context) else result

            else -> result
        }
    }

    private suspend fun kakaoTalkLogin(context: Context): SocialAuthResult =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                continuation.resume(
                    when {
                        error != null -> when {
                            // 사용자가 카카오톡 로그인 화면을 직접 닫음
                            error is ClientError && error.reason == ClientErrorCause.Cancelled ->
                                SocialAuthResult.Cancelled

                            else -> SocialAuthResult.Failure(FALLBACK_TO_ACCOUNT)
                        }

                        else -> token.toResult()
                    }
                )
            }
        }

    /**
     * 카카오계정(웹) 로그인.
     *
     * [selectAccount]이면 [Prompt.SELECT_ACCOUNT]를 붙여 **계정 선택 화면을 강제로 띄운다.**
     * 이 값이 없으면 kakao.com 세션이 살아 있을 때 화면 없이 기존 계정으로 바로 로그인된다.
     *
     * `UserApiClient.logout()`으로는 부족하다. 그건 앱이 가진 카카오 토큰만 만료시키고
     * kakao.com 웹 세션은 남겨서, 다시 로그인하면 또 같은 계정으로 붙는다.
     */
    private suspend fun kakaoAccountLogin(
        context: Context,
        selectAccount: Boolean = false,
    ): SocialAuthResult =
        suspendCancellableCoroutine { continuation ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                continuation.resume(
                    when {
                        error is ClientError && error.reason == ClientErrorCause.Cancelled ->
                            SocialAuthResult.Cancelled

                        error != null ->
                            SocialAuthResult.Failure(error.message ?: DEFAULT_FAILURE_MESSAGE)

                        else -> token.toResult()
                    }
                )
            }

            if (selectAccount) {
                UserApiClient.instance.loginWithKakaoAccount(
                    context,
                    prompts = listOf(Prompt.SELECT_ACCOUNT),
                    callback = callback,
                )
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }

    private fun OAuthToken?.toResult(): SocialAuthResult {
        val token = this?.accessToken.orEmpty()
        return if (token.isNotBlank()) {
            SocialAuthResult.Success(token)
        } else {
            SocialAuthResult.Failure(DEFAULT_FAILURE_MESSAGE)
        }
    }

    override fun record(provider: SocialProvider) {
        SocialSessionStore.provider = provider
    }

    /**
     * 로그아웃 — 기기에 남은 소셜 토큰만 지운다. **연동(동의)은 유지한다.**
     *
     * 연동이 남으므로 다시 로그인할 때 동의 화면은 뜨지 않는다. 이는 버그가 아니라 의도된 동작이다.
     * 동의 화면을 다시 보려면 카카오 계정 설정에서 직접 연결을 해제하거나 [unlink]를 타야 한다.
     */
    override fun logout() {
        when (SocialSessionStore.provider) {
            SocialProvider.KAKAO -> if (isKakaoConfigured) UserApiClient.instance.logout { }
            SocialProvider.EMAIL, null -> Unit
        }
        SocialSessionStore.provider = null
    }

    /**
     * 회원 탈퇴 — 제공자 서버의 **연동까지 해제**한다.
     *
     * 연동을 남겨 두면 탈퇴한 뒤에도 카카오의 "연결된 서비스" 목록에 오늘두가 계속 남고,
     * 나중에 다시 가입할 때 동의 화면도 뜨지 않는다.
     *
     * 서버 탈퇴가 이미 끝난 뒤의 뒷정리이므로 **실패해도 되돌리지 않는다.** 연동 해제에 실패했다고
     * 탈퇴를 취소할 수는 없으니, 로그만 남기고 진행한다(사용자는 계정 설정에서 직접 끊을 수 있다).
     */
    override suspend fun unlink() {
        when (SocialSessionStore.provider) {
            SocialProvider.KAKAO -> if (isKakaoConfigured) unlinkKakao()
            SocialProvider.EMAIL, null -> Unit
        }

        // 연동 해제에 실패했더라도 기기에 남은 소셜 세션은 반드시 정리한다.
        // 여기서 기록을 먼저 지우면 [logout]이 어느 SDK를 정리해야 할지 몰라 그냥 지나가고,
        // 탈퇴한 계정의 소셜 토큰이 기기에 그대로 남는다.
        logout()
    }

    private suspend fun unlinkKakao() = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.unlink { error ->
            if (error != null) Log.w(TAG, "카카오 연동 해제 실패", error)
            if (continuation.isActive) continuation.resume(Unit)
        }
    }

    private const val TAG = "SocialAuthClient"

    /** 카카오톡 로그인 실패 시 웹 로그인으로 넘어가라는 내부 신호. */
    private const val FALLBACK_TO_ACCOUNT = "__fallback_to_kakao_account__"
    private const val DEFAULT_FAILURE_MESSAGE = "소셜 로그인에 실패했어요. 잠시 후 다시 시도해주세요."
    private const val NOT_CONFIGURED_MESSAGE = "소셜 로그인 설정이 없어요. 관리자에게 문의해주세요."
}

package com.example.onuldo_fe.data.social

import android.content.Context
import com.example.onuldo_fe.BuildConfig
import com.example.onuldo_fe.data.auth.dto.SocialProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
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
 * 카카오·네이버 SDK 래퍼.
 *
 * 두 SDK 모두 콜백 기반이라 [suspendCancellableCoroutine]으로 감싸 ViewModel에서 코루틴으로 쓴다.
 * 여기서 얻은 `accessToken`을 서버 `POST /api/auth/oauth/login`에 넘기면 우리 서비스의 JWT가 발급된다.
 *
 * 키는 `local.properties` → `BuildConfig`로 주입된다. 키가 비어 있으면 [isConfigured]가 false가 되고
 * 화면은 소셜 로그인 버튼을 비활성화한다.
 */
object SocialAuthClient {

    private const val NAVER_CLIENT_NAME = "오늘두"

    private var initialized = false

    val isKakaoConfigured: Boolean get() = BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank()

    val isNaverConfigured: Boolean
        get() = BuildConfig.NAVER_CLIENT_ID.isNotBlank() &&
            BuildConfig.NAVER_CLIENT_SECRET.isNotBlank()

    fun isConfigured(provider: SocialProvider): Boolean = when (provider) {
        SocialProvider.KAKAO -> isKakaoConfigured
        SocialProvider.NAVER -> isNaverConfigured
        SocialProvider.EMAIL -> false
    }

    /** [OnuldoApplication]에서 앱 시작 시 1회 호출. */
    fun initialize(context: Context) {
        if (initialized) return
        initialized = true

        if (isKakaoConfigured) {
            KakaoSdk.init(context, BuildConfig.KAKAO_NATIVE_APP_KEY)
        }
        if (isNaverConfigured) {
            NaverIdLoginSDK.initialize(
                context,
                BuildConfig.NAVER_CLIENT_ID,
                BuildConfig.NAVER_CLIENT_SECRET,
                NAVER_CLIENT_NAME,
            )
        }
    }

    suspend fun login(context: Context, provider: SocialProvider): SocialAuthResult =
        when (provider) {
            SocialProvider.KAKAO -> loginWithKakao(context)
            SocialProvider.NAVER -> loginWithNaver(context)
            SocialProvider.EMAIL -> SocialAuthResult.Failure("지원하지 않는 로그인 방식이에요.")
        }

    /**
     * 카카오 로그인.
     *
     * 카카오톡 앱이 설치돼 있으면 앱으로, 없으면 웹(카카오계정)으로 로그인한다.
     * 카카오톡 로그인 중 사용자가 취소하면 웹으로 넘어가지 않고 그대로 취소 처리한다
     * (그렇지 않으면 취소했는데 웹 로그인 창이 다시 뜬다).
     */
    private suspend fun loginWithKakao(context: Context): SocialAuthResult {
        if (!isKakaoConfigured) return SocialAuthResult.Failure(NOT_CONFIGURED_MESSAGE)

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

    private suspend fun kakaoAccountLogin(context: Context): SocialAuthResult =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
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
        }

    private fun OAuthToken?.toResult(): SocialAuthResult {
        val token = this?.accessToken.orEmpty()
        return if (token.isNotBlank()) {
            SocialAuthResult.Success(token)
        } else {
            SocialAuthResult.Failure(DEFAULT_FAILURE_MESSAGE)
        }
    }

    private suspend fun loginWithNaver(context: Context): SocialAuthResult {
        if (!isNaverConfigured) return SocialAuthResult.Failure(NOT_CONFIGURED_MESSAGE)

        return suspendCancellableCoroutine { continuation ->
            NaverIdLoginSDK.authenticate(
                context,
                object : OAuthLoginCallback {
                    override fun onSuccess() {
                        val token = NaverIdLoginSDK.getAccessToken().orEmpty()
                        continuation.resume(
                            if (token.isNotBlank()) {
                                SocialAuthResult.Success(token)
                            } else {
                                SocialAuthResult.Failure(DEFAULT_FAILURE_MESSAGE)
                            }
                        )
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        continuation.resume(SocialAuthResult.Failure(message.ifBlank { DEFAULT_FAILURE_MESSAGE }))
                    }

                    override fun onError(errorCode: Int, message: String) {
                        // 네이버 SDK는 사용자 취소와 실제 오류를 모두 onError로 전달한다.
                        // 취소일 때만 조용히 넘기고, 나머지는 오류 문구를 보여준다.
                        val cancelled = message.equals(NAVER_USER_CANCEL, ignoreCase = true) ||
                            NaverIdLoginSDK.getLastErrorCode().code
                                .equals(NAVER_USER_CANCEL, ignoreCase = true)

                        continuation.resume(
                            if (cancelled) {
                                SocialAuthResult.Cancelled
                            } else {
                                SocialAuthResult.Failure(message.ifBlank { DEFAULT_FAILURE_MESSAGE })
                            }
                        )
                    }
                },
            )
        }
    }

    /** 소셜 계정 연결 해제 없이 로컬 세션만 정리한다. */
    fun logout(provider: SocialProvider) {
        when (provider) {
            SocialProvider.KAKAO -> if (isKakaoConfigured) UserApiClient.instance.logout { }
            SocialProvider.NAVER -> if (isNaverConfigured) NaverIdLoginSDK.logout()
            SocialProvider.EMAIL -> Unit
        }
    }

    /** 카카오톡 로그인 실패 시 웹 로그인으로 넘어가라는 내부 신호. */
    private const val FALLBACK_TO_ACCOUNT = "__fallback_to_kakao_account__"
    /** 네이버 SDK가 사용자 취소를 나타낼 때 쓰는 코드. */
    private const val NAVER_USER_CANCEL = "user_cancel"
    private const val DEFAULT_FAILURE_MESSAGE = "소셜 로그인에 실패했어요. 잠시 후 다시 시도해주세요."
    private const val NOT_CONFIGURED_MESSAGE = "소셜 로그인 설정이 없어요. 관리자에게 문의해주세요."
}

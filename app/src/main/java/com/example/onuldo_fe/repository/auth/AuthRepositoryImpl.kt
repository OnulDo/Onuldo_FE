package com.example.onuldo_fe.repository.auth

import com.example.onuldo_fe.data.auth.api.AuthApi
import com.example.onuldo_fe.data.auth.dto.EmailLoginRequest
import com.example.onuldo_fe.data.auth.dto.EmailSignupRequest
import com.example.onuldo_fe.data.auth.dto.OAuthLoginRequest
import com.example.onuldo_fe.data.auth.dto.OAuthSignupRequest
import com.example.onuldo_fe.data.auth.dto.SocialProvider
import com.example.onuldo_fe.data.auth.dto.TermAgreementRequest
import com.example.onuldo_fe.data.network.ApiErrorCode
import com.example.onuldo_fe.data.network.ApiResult
import com.example.onuldo_fe.data.network.AuthTokenResponse
import com.example.onuldo_fe.data.network.AuthTokens
import com.example.onuldo_fe.data.network.TokenRefreshApi
import com.example.onuldo_fe.data.network.TokenRefreshOutcome
import com.example.onuldo_fe.data.network.TokenStore
import com.example.onuldo_fe.data.network.executeRefresh
import com.example.onuldo_fe.data.network.jwtExpiryEpochSeconds
import com.example.onuldo_fe.data.network.safeApiCall
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
    private val tokenRefreshApi: TokenRefreshApi,
) : AuthRepository {

    override val isLoggedIn: Boolean get() = tokenStore.isLoggedIn

    override suspend fun login(email: String, password: String): ApiResult<Unit> =
        safeApiCall { authApi.login(EmailLoginRequest(email.trim(), password)) }
            .storeTokens()

    override suspend fun signup(
        email: String,
        password: String,
        nickname: String,
        profileImageUrl: String?,
        termAgreements: List<TermAgreementRequest>,
    ): ApiResult<Unit> = safeApiCall {
        authApi.signup(
            EmailSignupRequest(
                email = email.trim(),
                password = password,
                nickname = nickname.trim(),
                profileImageUrl = profileImageUrl,
                termAgreements = termAgreements,
            )
        )
    }.storeTokens()

    override suspend fun oauthLogin(
        provider: SocialProvider,
        socialAccessToken: String,
    ): ApiResult<OAuthLoginOutcome> {
        val result = safeApiCall { authApi.oauthLogin(OAuthLoginRequest(provider, socialAccessToken)) }

        return when (result) {
            is ApiResult.Success -> {
                val tokens = AuthTokenResponse(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                ).toTokensOrNull()

                if (tokens != null) tokenStore.update(tokens)
                ApiResult.Success(
                    OAuthLoginOutcome(
                        isNewUser = result.data.isNewUser,
                        loggedIn = tokens != null,
                    )
                )
            }

            is ApiResult.Failure -> result
            is ApiResult.NetworkError -> result
        }
    }

    override suspend fun oauthSignup(
        provider: SocialProvider,
        socialAccessToken: String,
        nickname: String,
        profileImageUrl: String?,
        termAgreements: List<TermAgreementRequest>,
    ): ApiResult<Unit> = safeApiCall {
        authApi.oauthSignup(
            OAuthSignupRequest(
                provider = provider,
                socialAccessToken = socialAccessToken,
                nickname = nickname.trim(),
                profileImageUrl = profileImageUrl,
                termAgreements = termAgreements,
            )
        )
    }.storeTokens()

    override fun logout() {
        tokenStore.clear()
    }

    override suspend fun restoreSession(): Boolean {
        val refreshToken = tokenStore.refreshToken
        if (refreshToken.isNullOrBlank()) return false

        // TokenRefreshApi는 Authenticator가 붙지 않은 클라이언트를 쓰는 동기 Call이라 IO로 옮겨 호출한다.
        return withContext(Dispatchers.IO) {
            try {
                when (val outcome = tokenRefreshApi.executeRefresh(refreshToken)) {
                    is TokenRefreshOutcome.Success -> {
                        tokenStore.update(outcome.tokens)
                        true
                    }

                    // 서버가 명시적으로 거부했다 — 리프레시 토큰도 만료됐으므로 재로그인이 필요하다.
                    TokenRefreshOutcome.Rejected -> {
                        tokenStore.clear()
                        false
                    }

                    // 서버 장애·통신 실패. 리프레시 토큰은 멀쩡할 수 있으므로 **지우지 않고**,
                    // 남아 있는 액세스 토큰이 아직 살아 있는지로 판단한다.
                    TokenRefreshOutcome.Transient -> {
                        Log.w(TAG, "세션 복구 재발급 실패(일시적) — 액세스 토큰 유효기간으로 판단")
                        hasUnexpiredAccessToken()
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // 여기서 예외가 새어 나가면 스플래시를 붙잡고 있는 화면이 **영원히 결과를 못 받는다**
                // (`StartupViewModel`의 `isSessionRestored`가 null로 남는다). 앱이 멈춘 것처럼 보이므로,
                // 예상 못 한 오류(암호화 저장소 쓰기 실패 등)는 랜딩 진입으로 처리한다.
                // 토큰은 지우지 않으므로 다음 실행에서 다시 복구를 시도할 수 있다.
                Log.w(TAG, "세션 복구 중 예기치 못한 오류 — 랜딩으로 진입", e)
                false
            }
        }
    }

    /**
     * 저장된 액세스 토큰이 아직 만료되지 않았는지. 재발급이 일시적으로 실패했을 때만 쓴다.
     *
     * 만료 시각을 못 읽으면 **일단 살아 있다고 본다.** 서버가 JWT가 아닌 토큰을 쓰도록 바뀌어도
     * 멀쩡한 사용자를 로그아웃시키지 않기 위해서다. 토큰이 실제로 죽었다면 첫 요청의 401을
     * [TokenAuthenticator]가 처리한다.
     */
    private fun hasUnexpiredAccessToken(): Boolean {
        val accessToken = tokenStore.accessToken
        if (accessToken.isNullOrBlank()) return false

        val expiry = jwtExpiryEpochSeconds(accessToken) ?: return true
        val nowSeconds = System.currentTimeMillis() / 1000
        // 화면 진입 직후 만료되는 것을 막기 위해 여유를 둔다.
        return expiry - EXPIRY_MARGIN_SECONDS > nowSeconds
    }

    /**
     * 토큰 응답을 저장소에 반영하고 결과를 [Unit]으로 바꾼다.
     * 성공 응답인데 토큰이 비어 있으면 로그인된 것으로 볼 수 없으므로 실패로 처리한다.
     */
    private fun ApiResult<AuthTokenResponse>.storeTokens(): ApiResult<Unit> = when (this) {
        is ApiResult.Success -> {
            val tokens: AuthTokens? = data.toTokensOrNull()
            if (tokens != null) {
                tokenStore.update(tokens)
                ApiResult.Success(Unit)
            } else {
                ApiResult.Failure(
                    code = ApiErrorCode.UNKNOWN,
                    message = ApiResult.DEFAULT_ERROR_MESSAGE,
                )
            }
        }

        is ApiResult.Failure -> this
        is ApiResult.NetworkError -> this
    }

    private companion object {
        const val TAG = "AuthRepository"

        /** 액세스 토큰 만료 판정 여유(초). 진입 직후 만료되는 상황을 막는다. */
        const val EXPIRY_MARGIN_SECONDS = 30
    }
}

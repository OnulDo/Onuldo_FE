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
import com.example.onuldo_fe.data.network.RefreshTokenRequest
import com.example.onuldo_fe.data.network.TokenRefreshApi
import com.example.onuldo_fe.data.network.TokenStore
import com.example.onuldo_fe.data.network.safeApiCall
import android.util.Log
import java.io.IOException
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
                val body = tokenRefreshApi.refresh(RefreshTokenRequest(refreshToken))
                    .execute()
                    .body()

                val tokens = if (body?.isSuccess == true) body.result?.toTokensOrNull() else null
                when {
                    tokens != null -> {
                        tokenStore.update(tokens)
                        true
                    }
                    // 서버가 명시적으로 거부했다 — 리프레시 토큰도 만료됐으므로 재로그인이 필요하다.
                    else -> {
                        tokenStore.clear()
                        false
                    }
                }
            } catch (e: IOException) {
                // 서버에 닿지 못했을 뿐이므로 세션을 유지한다.
                Log.w(TAG, "세션 복구 통신 실패 — 세션 유지", e)
                true
            } catch (e: Exception) {
                // 응답 파싱 실패 등. 서버가 거부했다는 근거가 아니라서 역시 세션을 지우지 않는다.
                Log.w(TAG, "세션 복구 응답 처리 실패 — 세션 유지", e)
                true
            }
        }
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
    }
}

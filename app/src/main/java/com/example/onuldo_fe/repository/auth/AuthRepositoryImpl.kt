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
import com.example.onuldo_fe.data.network.TokenStore
import com.example.onuldo_fe.data.network.safeApiCall

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
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
}

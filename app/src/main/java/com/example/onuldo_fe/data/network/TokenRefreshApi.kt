package com.example.onuldo_fe.data.network

import com.example.onuldo_fe.data.auth.dto.DeviceRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/** `POST /api/auth/refresh` 요청 본문. */
data class RefreshTokenRequest(
    val refreshToken: String,
    val device: DeviceRequest,
)

/**
 * 로그인·회원가입·토큰재발급이 공통으로 돌려주는 토큰 쌍(`AuthResDto`).
 * Phase 1의 인증 API도 이 모델을 재사용한다.
 */
data class AuthTokenResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null,
) {
    /** 두 토큰이 모두 있을 때만 유효한 토큰 쌍으로 본다. */
    fun toTokensOrNull(): AuthTokens? {
        val access = accessToken?.trim().orEmpty()
        val refresh = refreshToken?.trim().orEmpty()
        return if (access.isNotEmpty() && refresh.isNotEmpty()) AuthTokens(access, refresh) else null
    }
}

/**
 * 토큰 재발급 전용 API.
 *
 * [TokenAuthenticator]가 401을 만났을 때 호출한다. 재발급 요청 자체가 다시 401을 받아
 * 무한 재귀에 빠지지 않도록, **Authenticator가 붙지 않은 별도 OkHttp 클라이언트**로 호출된다.
 * Authenticator는 코루틴이 아닌 동기 컨텍스트에서 동작하므로 `suspend`가 아닌 [Call]을 쓴다.
 */
interface TokenRefreshApi {
    @POST("api/auth/refresh")
    fun refresh(@Body request: RefreshTokenRequest): Call<BaseResponse<AuthTokenResponse>>
}

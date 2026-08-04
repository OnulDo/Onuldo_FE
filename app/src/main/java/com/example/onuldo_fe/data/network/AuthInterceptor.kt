package com.example.onuldo_fe.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 모든 요청에 `Authorization: Bearer {accessToken}` 헤더를 붙인다.
 *
 * 로그인·회원가입·토큰재발급은 토큰이 없는 상태에서 호출되므로 헤더를 붙이지 않는다.
 * (붙여도 서버가 무시하지만, 만료된 토큰 때문에 불필요한 401이 나는 것을 막는다.)
 */
class AuthInterceptor(
    private val tokenStore: TokenStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.encodedPath.isAuthFreePath()) {
            return chain.proceed(request)
        }

        val token = tokenStore.accessToken
        if (token.isNullOrBlank()) {
            return chain.proceed(request)
        }

        val authorized = request.newBuilder()
            .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$token")
            .build()
        return chain.proceed(authorized)
    }

    private fun String.isAuthFreePath(): Boolean =
        AUTH_FREE_PATHS.any { this.endsWith(it) }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "

        /** 토큰 없이 호출되는 경로. */
        private val AUTH_FREE_PATHS = listOf(
            "/api/auth/login",
            "/api/auth/signup",
            "/api/auth/refresh",
            "/api/auth/oauth/login",
            "/api/auth/oauth/signup",
        )
    }
}

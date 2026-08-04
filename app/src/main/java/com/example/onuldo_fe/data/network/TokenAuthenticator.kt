package com.example.onuldo_fe.data.network

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * 액세스 토큰 만료(401) 시 리프레시 토큰으로 자동 재발급하고 원래 요청을 재시도한다.
 *
 * 서버 액세스 토큰 수명이 **30분**이라 이 처리가 없으면 앱을 켜둔 채 30분이 지나는 순간
 * 모든 API가 401로 실패한다. 리프레시는 14일이라 그 안에서는 재로그인 없이 이어진다.
 *
 * 동작:
 * 1. 이미 한 번 재시도한 요청이면 포기한다(무한 루프 방지).
 * 2. 다른 스레드가 먼저 갱신해 토큰이 이미 바뀌었으면, 재발급 없이 새 토큰으로 재시도한다.
 * 3. 재발급에 성공하면 저장소를 갱신하고 원래 요청을 새 토큰으로 재시도한다.
 * 4. 재발급에 실패하면(리프레시도 만료 등) 토큰을 비우고 [SessionEvents]로 만료를 알린다.
 */
class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val refreshApiProvider: () -> TokenRefreshApi,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. 재시도한 요청이 또 401이면 재발급으로 해결되지 않는 상황이다.
        if (response.priorResponseCount() >= MAX_RETRY_COUNT) {
            expireSession()
            return null
        }

        val failedToken = response.request.header(AuthInterceptor.HEADER_AUTHORIZATION)
            ?.removePrefix(AuthInterceptor.BEARER_PREFIX)

        synchronized(this) {
            val currentToken = tokenStore.accessToken

            // 2. 다른 요청이 이미 갱신을 끝냈다면 그 토큰으로 바로 재시도한다.
            if (!currentToken.isNullOrBlank() && currentToken != failedToken) {
                return response.request.withToken(currentToken)
            }

            val refreshToken = tokenStore.refreshToken
            if (refreshToken.isNullOrBlank()) {
                expireSession()
                return null
            }

            // 3. 재발급 시도.
            val newTokens = requestNewTokens(refreshToken)
            if (newTokens == null) {
                expireSession()
                return null
            }

            tokenStore.update(newTokens)
            return response.request.withToken(newTokens.accessToken)
        }
    }

    /** 동기 호출. Authenticator는 코루틴이 아닌 OkHttp 워커 스레드에서 실행된다. */
    private fun requestNewTokens(refreshToken: String): AuthTokens? =
        try {
            val body = refreshApiProvider()
                .refresh(RefreshTokenRequest(refreshToken))
                .execute()
                .body()

            if (body?.isSuccess == true) body.result?.toTokensOrNull() else null
        } catch (e: Exception) {
            // 네트워크 오류로 재발급에 실패한 경우도 여기로 온다. 세션은 만료 처리한다.
            null
        }

    /**
     * 세션을 정리하고 만료를 알린다.
     *
     * 서버는 토큰이 아예 없는 요청에도 401(`UNAUTHORIZED`)을 주기 때문에, 로그인한 적 없는 상태의
     * 401까지 만료로 알리면 랜딩 화면에서 불필요한 신호가 발생한다.
     * **원래 세션이 있었을 때만** 알린다.
     */
    private fun expireSession() {
        val hadSession = tokenStore.accessToken != null || tokenStore.refreshToken != null
        tokenStore.clear()
        if (hadSession) {
            SessionEvents.notifySessionExpired()
        }
    }

    private fun Request.withToken(token: String): Request =
        newBuilder()
            .header(AuthInterceptor.HEADER_AUTHORIZATION, "${AuthInterceptor.BEARER_PREFIX}$token")
            .build()

    /** 이 응답에 이르기까지 재시도된 횟수. */
    private fun Response.priorResponseCount(): Int {
        var count = 0
        var prior = priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    companion object {
        private const val MAX_RETRY_COUNT = 1
    }
}

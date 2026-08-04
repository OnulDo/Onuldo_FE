package com.example.onuldo_fe.data.network

import android.util.Log
import java.io.IOException
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

            // 방금 같은 토큰으로 재발급이 통신 실패했다면 다시 시도하지 않는다.
            // 연결이 끊긴 상태에서 401이 여러 개 몰리면 요청 수만큼 30초씩 직렬로 대기하게 된다.
            if (isRecentTransientFailure(refreshToken)) {
                return null
            }

            // 3. 재발급 시도.
            return when (val outcome = requestNewTokens(refreshToken)) {
                is RefreshOutcome.Success -> {
                    tokenStore.update(outcome.tokens)
                    response.request.withToken(outcome.tokens.accessToken)
                }

                // 서버가 재발급을 거부했다 — 리프레시 토큰도 만료됐으므로 재로그인이 필요하다.
                RefreshOutcome.Rejected -> {
                    expireSession()
                    null
                }

                // 통신 자체가 실패했다. 리프레시 토큰은 아직 유효할 수 있으므로 세션을 지우지 않고
                // 이 요청만 실패시킨다. (토큰이 메모리에만 있어 지우면 복구 경로가 없다.)
                // 뒤따르는 요청들이 같은 실패를 반복하지 않도록 결과를 잠시 기억해 둔다.
                RefreshOutcome.Transient -> {
                    markTransientFailure(refreshToken)
                    null
                }
            }
        }
    }

    /**
     * 직전에 통신 실패한 리프레시 토큰과 그 시각. 동시에 몰린 401들이 같은 실패를
     * 반복하지 않도록 짧은 시간 동안 공유한다. 접근은 [authenticate]의 synchronized 블록 안에서만 한다.
     */
    private var lastFailedRefreshToken: String? = null
    private var lastFailedAtMillis: Long = 0L

    private fun isRecentTransientFailure(refreshToken: String): Boolean =
        lastFailedRefreshToken == refreshToken &&
            System.currentTimeMillis() - lastFailedAtMillis < TRANSIENT_FAILURE_WINDOW_MS

    private fun markTransientFailure(refreshToken: String) {
        lastFailedRefreshToken = refreshToken
        lastFailedAtMillis = System.currentTimeMillis()
    }

    /** 재발급 시도 결과. 서버의 거부와 통신 실패를 구분해야 세션을 잘못 만료시키지 않는다. */
    private sealed interface RefreshOutcome {
        data class Success(val tokens: AuthTokens) : RefreshOutcome
        data object Rejected : RefreshOutcome
        data object Transient : RefreshOutcome
    }

    /** 동기 호출. Authenticator는 코루틴이 아닌 OkHttp 워커 스레드에서 실행된다. */
    private fun requestNewTokens(refreshToken: String): RefreshOutcome =
        try {
            val body = refreshApiProvider()
                .refresh(RefreshTokenRequest(refreshToken))
                .execute()
                .body()

            val tokens = if (body?.isSuccess == true) body.result?.toTokensOrNull() else null
            if (tokens != null) RefreshOutcome.Success(tokens) else RefreshOutcome.Rejected
        } catch (e: IOException) {
            // 연결 끊김·타임아웃 등. 서버 판단이 아니므로 세션을 유지한다.
            Log.w(TAG, "토큰 재발급 통신 실패 — 세션 유지", e)
            RefreshOutcome.Transient
        } catch (e: Exception) {
            // 응답 파싱 실패 등. 서버가 리프레시 토큰을 거부했다는 근거가 아니므로
            // 세션을 지우지 않는다(잘못 지우면 메모리 저장소 특성상 복구할 수 없다).
            Log.w(TAG, "토큰 재발급 응답 처리 실패 — 세션 유지", e)
            RefreshOutcome.Transient
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
        private const val TAG = "TokenAuthenticator"
        private const val MAX_RETRY_COUNT = 1

        /** 통신 실패한 재발급을 다시 시도하지 않고 넘기는 시간. */
        private const val TRANSIENT_FAILURE_WINDOW_MS = 3_000L
    }
}

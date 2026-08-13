package com.example.onuldo_fe.data.network

import java.io.IOException
import java.util.Base64
import kotlinx.coroutines.CancellationException

/**
 * 토큰 재발급 시도 결과.
 *
 * **[Rejected]와 [Transient]를 반드시 구분해야 한다.** [Rejected]는 세션을 지우지만
 * [Transient]는 세션을 유지한다. 서버 장애(5xx)를 [Rejected]로 처리하면 멀쩡한 리프레시
 * 토큰을 가진 사용자가 로그아웃된다.
 *
 * 실제로 서버 `POST /api/auth/refresh`가 500(`Cannot execute statement in a READ ONLY
 * transaction`)을 돌려주는 동안 자동 로그인이 전혀 되지 않았다. 이 구분이 없으면 앱을 켤 때마다
 * 저장된 토큰이 지워졌기 때문이다. (2026-08-08 확인, 백엔드 수정 요청함)
 */
sealed interface TokenRefreshOutcome {
    data class Success(val tokens: AuthTokens) : TokenRefreshOutcome

    /** 서버가 리프레시 토큰을 명시적으로 거부했다(401·403·무효 토큰 코드). 재로그인이 필요하다. */
    data object Rejected : TokenRefreshOutcome

    /** 서버 장애·통신 실패·응답 해석 실패. 토큰이 무효라는 근거가 아니므로 세션을 유지한다. */
    data object Transient : TokenRefreshOutcome
}

/**
 * 토큰 재발급을 **동기로** 호출한다. [TokenAuthenticator](OkHttp 워커 스레드)와
 * 세션 복구가 같은 판정을 쓰도록 한 곳에 모았다.
 *
 * 판정 기준은 **HTTP 상태와 에러 코드**다. 응답 본문이 없다는 이유만으로 거부로 보면
 * 5xx가 곧 로그아웃이 된다.
 */
fun TokenRefreshApi.executeRefresh(
    refreshToken: String,
    device: com.example.onuldo_fe.data.auth.dto.DeviceRequest,
): TokenRefreshOutcome =
    try {
        val response = refresh(RefreshTokenRequest(refreshToken, device)).execute()
        val body = response.body()
        val tokens = if (body?.isSuccess == true) body.result?.toTokensOrNull() else null

        when {
            tokens != null -> TokenRefreshOutcome.Success(tokens)

            // 인증 실패 = 리프레시 토큰이 만료·무효하다는 서버의 명시적 판단.
            response.code() == HTTP_UNAUTHORIZED || response.code() == HTTP_FORBIDDEN ->
                TokenRefreshOutcome.Rejected

            // 본문에 무효 토큰 코드가 실려 온 경우(200으로 내려주는 서버도 있다).
            ApiErrorCode.isTokenInvalid(body?.code) -> TokenRefreshOutcome.Rejected

            // 그 밖의 실패(5xx 등)는 서버 사정이므로 세션을 지우지 않는다.
            else -> TokenRefreshOutcome.Transient
        }
    } catch (e: IOException) {
        // 연결 끊김·타임아웃(callTimeout 포함). 서버 판단이 아니다.
        TokenRefreshOutcome.Transient
    } catch (e: CancellationException) {
        // 코루틴 취소는 실패가 아니라 상위로 전파해야 한다.
        // (`CancellationException`도 `Exception`이라 아래 catch가 삼키면 구조적 동시성이 깨진다.
        //  `safeApiCall`의 `runCatchingApi`도 같은 이유로 이를 먼저 다시 던진다.)
        throw e
    } catch (e: Exception) {
        // 응답 파싱 실패 등. 토큰이 무효하다는 근거가 아니다.
        TokenRefreshOutcome.Transient
    }

private const val HTTP_UNAUTHORIZED = 401
private const val HTTP_FORBIDDEN = 403

/**
 * JWT의 만료 시각(`exp`, epoch 초)을 읽는다. 서명은 검증하지 않는다 —
 * 보안 판단이 아니라 **"이 토큰으로 요청을 시도할 가치가 있는가"** 판단에만 쓴다.
 *
 * 재발급이 [TokenRefreshOutcome.Transient]로 실패했을 때, 남아 있는 액세스 토큰이
 * 아직 살아 있는지 보고 자동 로그인 여부를 정하는 데 필요하다.
 */
fun jwtExpiryEpochSeconds(token: String): Long? = try {
    val payload = token.split('.').getOrNull(1)
    if (payload.isNullOrEmpty()) {
        null
    } else {
        // JWT는 padding 없는 base64url이라 디코더가 요구하는 만큼 '='를 채워 준다.
        val padded = payload.padEnd((payload.length + 3) / 4 * 4, '=')
        val json = String(Base64.getUrlDecoder().decode(padded), Charsets.UTF_8)
        Regex("\"exp\"\\s*:\\s*(\\d+)").find(json)?.groupValues?.get(1)?.toLong()
    }
} catch (e: Exception) {
    null
}

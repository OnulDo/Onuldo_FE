package com.example.onuldo_fe.repository.auth

import com.example.onuldo_fe.data.auth.api.AuthApi
import com.example.onuldo_fe.data.auth.dto.EmailLoginRequest
import com.example.onuldo_fe.data.auth.dto.EmailSignupRequest
import com.example.onuldo_fe.data.auth.dto.OAuthLoginRequest
import com.example.onuldo_fe.data.auth.dto.OAuthLoginResponse
import com.example.onuldo_fe.data.auth.dto.OAuthSignupRequest
import com.example.onuldo_fe.data.network.AuthTokenResponse
import com.example.onuldo_fe.data.network.AuthTokens
import com.example.onuldo_fe.data.network.BaseResponse
import com.example.onuldo_fe.data.network.RefreshTokenRequest
import com.example.onuldo_fe.data.network.TokenRefreshApi
import com.example.onuldo_fe.data.network.TokenStore
import java.io.IOException
import java.util.Base64
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Timeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 자동 로그인(세션 복구) 판정 검증 — QA 시나리오 "스플래시·자동 로그인 분기" 1.0·2.0.
 *
 * 판정이 틀리면 로그인한 사용자가 랜딩으로 떨어지거나(1.0 실패),
 * 만료된 세션으로 홈에 들어갔다가 튕긴다(2.0 실패).
 */
class AuthRepositoryImplRestoreSessionTest {

    @Test
    fun `리프레시 토큰이 없으면 세션을 복구하지 않는다`() = runBlocking {
        val store = FakeTokenStore()
        val api = FakeTokenRefreshApi(response = successResponse())
        val repository = repository(store, api)

        assertFalse(repository.restoreSession())
        // 로그인한 적이 없으므로 재발급 요청 자체를 보내지 않아야 한다.
        assertEquals(0, api.callCount)
    }

    @Test
    fun `재발급에 성공하면 새 토큰을 저장하고 세션을 복구한다`() = runBlocking {
        val store = FakeTokenStore().apply {
            update(AuthTokens("expiredAccess", "validRefresh"))
        }
        val api = FakeTokenRefreshApi(response = successResponse("newAccess", "newRefresh"))
        val repository = repository(store, api)

        assertTrue(repository.restoreSession())
        assertEquals("newAccess", store.accessToken)
        assertEquals("newRefresh", store.refreshToken)
        assertEquals("validRefresh", api.lastSentRefreshToken)
    }

    @Test
    fun `서버가 401로 거부하면 토큰을 비우고 세션을 복구하지 않는다`() = runBlocking {
        // 리프레시 토큰(14일)까지 만료된 상황 — 재로그인이 필요하다.
        val store = FakeTokenStore().apply {
            update(AuthTokens("expiredAccess", "expiredRefresh"))
        }
        val api = FakeTokenRefreshApi(response = errorResponse(401, "INVALID_TOKEN"))
        val repository = repository(store, api)

        assertFalse(repository.restoreSession())
        assertNull(store.accessToken)
        assertNull(store.refreshToken)
    }

    @Test
    fun `본문에 무효 토큰 코드가 오면 거부로 본다`() = runBlocking {
        val store = FakeTokenStore().apply { update(AuthTokens("access", "refresh")) }
        val api = FakeTokenRefreshApi(
            response = Response.success(
                BaseResponse<AuthTokenResponse>(code = "TOKEN_EXPIRED", message = "만료된 토큰입니다.")
            )
        )
        val repository = repository(store, api)

        assertFalse(repository.restoreSession())
        assertNull(store.refreshToken)
    }

    @Test
    fun `서버 오류(500)에는 세션을 지우지 않는다`() = runBlocking {
        // 실제로 겪은 상황 — 서버 refresh가 500(READ ONLY transaction)을 돌려줬다.
        // 이를 거부로 처리하면 멀쩡한 사용자가 앱을 켤 때마다 로그아웃된다.
        val store = FakeTokenStore().apply {
            update(AuthTokens(jwt(expiresInSeconds = 600), "validRefresh"))
        }
        val api = FakeTokenRefreshApi(response = errorResponse(500, "INTERNAL_SERVER_ERROR"))
        val repository = repository(store, api)

        // 액세스 토큰이 아직 살아 있으므로 홈으로 보낸다.
        assertTrue(repository.restoreSession())
        assertEquals("validRefresh", store.refreshToken)
    }

    @Test
    fun `서버 오류이고 액세스 토큰도 만료됐으면 세션을 복구하지 않는다`() = runBlocking {
        val store = FakeTokenStore().apply {
            update(AuthTokens(jwt(expiresInSeconds = -60), "validRefresh"))
        }
        val api = FakeTokenRefreshApi(response = errorResponse(500, "INTERNAL_SERVER_ERROR"))
        val repository = repository(store, api)

        assertFalse(repository.restoreSession())
        // 서버가 거부한 게 아니므로 리프레시 토큰은 남겨 둔다(서버 복구 후 다시 시도할 수 있다).
        assertEquals("validRefresh", store.refreshToken)
    }

    @Test
    fun `통신에 실패하면 세션을 지우지 않고 유지한다`() = runBlocking {
        // 비행기모드로 앱을 켠 경우. 서버가 거부한 게 아니므로 로그아웃시키면 안 된다.
        val store = FakeTokenStore().apply {
            update(AuthTokens(jwt(expiresInSeconds = 600), "refresh"))
        }
        val api = FakeTokenRefreshApi(error = IOException("네트워크 없음"))
        val repository = repository(store, api)

        assertTrue(repository.restoreSession())
        assertEquals("refresh", store.refreshToken)
    }

    @Test
    fun `예기치 못한 오류가 나도 예외를 던지지 않는다`() = runBlocking {
        // 예외가 새어 나가면 스플래시가 결과를 못 받아 앱이 멈춘 것처럼 보인다.
        // 토큰 저장 중 실패해도 랜딩으로 진입시키고 끝내야 한다.
        val store = object : FakeTokenStore() {
            override fun update(tokens: AuthTokens): Unit = throw IllegalStateException("저장소 쓰기 실패")
        }.apply { seed(AuthTokens("access", "refresh")) }
        val api = FakeTokenRefreshApi(response = successResponse())

        assertFalse(repository(store, api).restoreSession())
    }

    @Test
    fun `코루틴 취소는 삼키지 않고 그대로 전파한다`() {
        val store = FakeTokenStore().apply { update(AuthTokens("access", "refresh")) }
        val api = FakeTokenRefreshApi(error = null, response = null, cancel = true)

        var propagated = false
        try {
            runBlocking { repository(store, api).restoreSession() }
        } catch (e: CancellationException) {
            propagated = true
        }
        assertTrue(propagated)
    }

    @Test
    fun `만료 시각을 읽을 수 없는 토큰은 살아 있는 것으로 본다`() = runBlocking {
        // 서버가 JWT가 아닌 토큰으로 바뀌어도 멀쩡한 사용자를 로그아웃시키지 않는다.
        val store = FakeTokenStore().apply { update(AuthTokens("opaque-token", "refresh")) }
        val api = FakeTokenRefreshApi(error = IOException("네트워크 없음"))

        assertTrue(repository(store, api).restoreSession())
    }

    // ===== 테스트 더블 =====

    private fun repository(store: TokenStore, refreshApi: TokenRefreshApi): AuthRepository =
        AuthRepositoryImpl(UnusedAuthApi, store, refreshApi)

    private fun successResponse(
        access: String = "newAccess",
        refresh: String = "newRefresh",
    ): Response<BaseResponse<AuthTokenResponse>> = Response.success(
        BaseResponse(code = "SUCCESS", result = AuthTokenResponse(access, refresh))
    )

    private fun errorResponse(
        httpStatus: Int,
        code: String,
    ): Response<BaseResponse<AuthTokenResponse>> = Response.error(
        httpStatus,
        """{"code":"$code","message":"오류"}"""
            .toResponseBody("application/json".toMediaType()),
    )

    /** `exp`만 담긴 서명 없는 JWT. 만료 판정은 서명을 검증하지 않으므로 이걸로 충분하다. */
    private fun jwt(expiresInSeconds: Long): String {
        val exp = System.currentTimeMillis() / 1000 + expiresInSeconds
        val encoder = Base64.getUrlEncoder().withoutPadding()
        val header = encoder.encodeToString("""{"alg":"HS256"}""".toByteArray())
        val payload = encoder.encodeToString("""{"sub":"tester","exp":$exp}""".toByteArray())
        return "$header.$payload.signature"
    }

    private open class FakeTokenStore : TokenStore {
        private var access: String? = null
        private var refresh: String? = null

        override val accessToken: String? get() = access
        override val refreshToken: String? get() = refresh

        override fun update(tokens: AuthTokens) {
            access = tokens.accessToken.trim().takeIf(String::isNotEmpty)
            refresh = tokens.refreshToken.trim().takeIf(String::isNotEmpty)
        }

        override fun clear() {
            access = null
            refresh = null
        }

        /** [update]를 재정의한 하위 클래스도 초기값을 넣을 수 있게 한다. */
        fun seed(tokens: AuthTokens) {
            access = tokens.accessToken
            refresh = tokens.refreshToken
        }
    }

    private class FakeTokenRefreshApi(
        private val response: Response<BaseResponse<AuthTokenResponse>>? = null,
        private val error: IOException? = null,
        private val cancel: Boolean = false,
    ) : TokenRefreshApi {
        var callCount = 0
            private set
        var lastSentRefreshToken: String? = null
            private set

        override fun refresh(request: RefreshTokenRequest): Call<BaseResponse<AuthTokenResponse>> {
            callCount++
            lastSentRefreshToken = request.refreshToken
            return FakeCall(response, error, cancel)
        }
    }

    /** `execute()`만 쓰는 최소 구현. retrofit-mock 의존성을 추가하지 않기 위해 직접 만든다. */
    private class FakeCall<T>(
        private val response: Response<T>?,
        private val error: IOException?,
        private val cancel: Boolean = false,
    ) : Call<T> {
        override fun execute(): Response<T> {
            if (cancel) throw CancellationException("취소됨")
            error?.let { throw it }
            return requireNotNull(response) { "response 또는 error 중 하나는 있어야 한다" }
        }

        override fun enqueue(callback: Callback<T>) = throw UnsupportedOperationException()
        override fun clone(): Call<T> = FakeCall(response, error)
        override fun isExecuted(): Boolean = false
        override fun isCanceled(): Boolean = false
        override fun cancel() = Unit
        override fun request(): Request = Request.Builder().url("https://onuldo.site/").build()
        override fun timeout(): Timeout = Timeout.NONE
    }

    /** 세션 복구는 인증 API를 쓰지 않는다. 불리면 테스트가 잘못된 것이므로 즉시 실패시킨다. */
    private object UnusedAuthApi : AuthApi {
        override suspend fun emailExists(email: String) = error("호출되면 안 된다")
        override suspend fun login(request: EmailLoginRequest) = error("호출되면 안 된다")
        override suspend fun signup(request: EmailSignupRequest) = error("호출되면 안 된다")
        override suspend fun oauthLogin(request: OAuthLoginRequest): Response<BaseResponse<OAuthLoginResponse>> =
            error("호출되면 안 된다")

        override suspend fun oauthSignup(request: OAuthSignupRequest) = error("호출되면 안 된다")
    }
}

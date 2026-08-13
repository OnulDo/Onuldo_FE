package com.example.onuldo_fe.data.network

/** 로그인 성공 시 서버가 내려주는 토큰 쌍. */
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)

/**
 * 액세스/리프레시 토큰 보관소.
 *
 * 서버 정책상 **access는 30분, refresh는 14일**이라, access 만료 시 [TokenAuthenticator]가
 * refresh로 자동 재발급한다. 화면·Repository는 토큰을 직접 다루지 않고 이 저장소만 갱신하면 된다.
 *
 * 현재 구현([InMemoryTokenStore])은 메모리 보관이라 앱을 재시작하면 사라진다.
 * 자동 로그인을 도입하면 DataStore 기반 구현으로 교체하면 되고, 이 인터페이스만 유지하면
 * 호출부는 수정할 필요가 없다.
 */
interface TokenStore {
    val accessToken: String?
    val refreshToken: String?

    fun update(tokens: AuthTokens)
    fun clear()

    val isLoggedIn: Boolean get() = !accessToken.isNullOrBlank()
}

/** 메모리 전용 토큰 보관소. 정적 토큰을 앱 바이너리에 포함하지 않는다. */
object InMemoryTokenStore : TokenStore {

    @Volatile
    private var _accessToken: String? = null

    @Volatile
    private var _refreshToken: String? = null

    override val accessToken: String? get() = _accessToken
    override val refreshToken: String? get() = _refreshToken

    @Synchronized
    override fun update(tokens: AuthTokens) {
        _accessToken = tokens.accessToken.trim().takeIf(String::isNotEmpty)
        _refreshToken = tokens.refreshToken.trim().takeIf(String::isNotEmpty)
    }

    @Synchronized
    override fun clear() {
        _accessToken = null
        _refreshToken = null
    }
}

package com.example.onuldo_fe.data.party.network

/**
 * 로그인 성공 후 전달받은 사용자별 access token만 메모리에 보관한다.
 * 정적 토큰을 앱 바이너리에 포함하지 않으며, 로그아웃 시 clear()로 즉시 제거한다.
 */
object PartyAccessTokenStore {
    @Volatile
    private var accessToken: String? = null

    fun get(): String? = accessToken

    fun update(token: String?) {
        accessToken = token?.trim()?.takeIf(String::isNotEmpty)
    }

    fun clear() {
        accessToken = null
    }
}

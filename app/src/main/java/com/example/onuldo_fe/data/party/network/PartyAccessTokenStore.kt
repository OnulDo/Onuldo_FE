package com.example.onuldo_fe.data.party.network

import com.example.onuldo_fe.BuildConfig

/**
 * 로그인 연동 전에는 Gradle 속성 또는 환경 변수 PARTY_API_ACCESS_TOKEN으로 토큰을 주입한다.
 * 공통 인증 계층이 추가되면 해당 토큰 제공자로 교체한다.
 */
object PartyAccessTokenStore {
    @Volatile
    private var accessToken: String? = BuildConfig.PARTY_API_ACCESS_TOKEN.takeIf(String::isNotBlank)

    fun get(): String? = accessToken

    fun update(token: String?) {
        accessToken = token?.trim()?.takeIf(String::isNotEmpty)
    }

    fun clear() {
        accessToken = null
    }
}

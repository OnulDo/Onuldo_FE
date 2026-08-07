package com.example.onuldo_fe.repository.auth

import com.example.onuldo_fe.data.auth.api.AuthApi
import com.example.onuldo_fe.data.network.NetworkModule

/** 인증 저장소 제공자. 기존 `*RepositoryProvider` 패턴과 동일한 방식. */
object AuthRepositoryProvider {

    private val authApi: AuthApi by lazy { NetworkModule.create(AuthApi::class.java) }

    private val repository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi, NetworkModule.tokenStore, NetworkModule.tokenRefreshApi)
    }

    fun provide(): AuthRepository = repository
}

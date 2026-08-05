package com.example.onuldo_fe.repository.user

import com.example.onuldo_fe.data.network.NetworkModule
import com.example.onuldo_fe.data.user.api.UserApi

object UserRepositoryProvider {

    private val userApi: UserApi by lazy { NetworkModule.create(UserApi::class.java) }

    private val repository: UserRepository by lazy { UserRepositoryImpl(userApi) }

    fun provide(): UserRepository = repository
}

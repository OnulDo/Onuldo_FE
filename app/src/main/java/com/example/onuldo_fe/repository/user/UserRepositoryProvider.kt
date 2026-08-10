package com.example.onuldo_fe.repository.user

import com.example.onuldo_fe.data.network.NetworkModule
import com.example.onuldo_fe.data.notification.api.NotificationApi
import com.example.onuldo_fe.data.user.api.UserApi

object UserRepositoryProvider {

    private val userApi: UserApi by lazy { NetworkModule.create(UserApi::class.java) }
    private val notificationApi: NotificationApi by lazy { NetworkModule.create(NotificationApi::class.java) }

    private val repository: UserRepository by lazy { UserRepositoryImpl(userApi, notificationApi) }

    fun provide(): UserRepository = repository
}

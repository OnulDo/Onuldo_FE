package com.example.onuldo_fe.repository.notification

import com.example.onuldo_fe.data.network.NetworkModule
import com.example.onuldo_fe.data.notification.api.NotificationApi

object NotificationRepositoryProvider {
    private val api: NotificationApi by lazy { NetworkModule.create(NotificationApi::class.java) }

    fun provide(): NotificationRepository = NotificationRepositoryImpl(api)
}

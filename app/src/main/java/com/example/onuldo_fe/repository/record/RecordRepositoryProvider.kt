package com.example.onuldo_fe.repository.record

import com.example.onuldo_fe.data.network.NetworkModule
import com.example.onuldo_fe.data.record.api.RecordApi

object RecordRepositoryProvider {
    private val repository: RecordRepository by lazy {
        val api = NetworkModule.create(RecordApi::class.java)
        RecordRepositoryImpl(api)
    }

    fun create(): RecordRepository = repository
}

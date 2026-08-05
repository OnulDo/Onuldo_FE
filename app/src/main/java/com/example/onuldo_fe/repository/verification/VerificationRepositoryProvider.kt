package com.example.onuldo_fe.repository.verification

import android.content.Context
import com.example.onuldo_fe.data.network.NetworkModule
import com.example.onuldo_fe.data.verification.api.VerificationApi

object VerificationRepositoryProvider {
    fun create(context: Context): VerificationRepository {
        val api = NetworkModule.create(VerificationApi::class.java)
        return VerificationRepositoryImpl(context.applicationContext, api)
    }
}

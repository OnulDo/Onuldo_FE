package com.example.onuldo_fe.data.auth

import android.content.Context
import com.example.onuldo_fe.data.auth.dto.DeviceRequest
import com.google.firebase.messaging.FirebaseMessaging
import java.util.UUID
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/** 앱 설치 식별자와 FCM 토큰을 한 곳에서 관리한다. */
fun interface DeviceInfoSource {
    suspend fun getDevice(): DeviceRequest
}

class DeviceInfoProvider private constructor(context: Context) : DeviceInfoSource {
    private val preferences = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val deviceId: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        preferences.getString(KEY_DEVICE_ID, null)?.takeIf(String::isNotBlank)
            ?: UUID.randomUUID().toString().also {
                // apply도 메모리 값은 즉시 바꾸므로 동기화 구간 이후 모든 호출이 같은 값을 본다.
                preferences.edit().putString(KEY_DEVICE_ID, it).apply()
            }
    }

    override suspend fun getDevice(): DeviceRequest {
        val latestToken = suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (continuation.isActive) {
                    continuation.resume(if (task.isSuccessful) task.result.orEmpty() else "")
                }
            }
        }
        if (latestToken.isNotBlank()) saveFcmToken(latestToken)

        return DeviceRequest(
            deviceId = deviceId,
            fcmToken = latestToken.ifBlank {
                preferences.getString(KEY_FCM_TOKEN, "").orEmpty()
            },
        )
    }

    fun saveFcmToken(token: String) {
        if (token.isNotBlank()) preferences.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    companion object {
        private const val PREFS_NAME = "auth_device"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_FCM_TOKEN = "fcm_token"

        @Volatile private var instance: DeviceInfoProvider? = null

        fun initialize(context: Context) {
            if (instance == null) synchronized(this) {
                if (instance == null) instance = DeviceInfoProvider(context)
            }
        }

        fun get(): DeviceInfoProvider = checkNotNull(instance) {
            "DeviceInfoProvider.initialize must be called first"
        }
    }
}

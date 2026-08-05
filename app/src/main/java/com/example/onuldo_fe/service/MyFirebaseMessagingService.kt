package com.example.onuldo_fe.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// FCM 수신 서비스 — 토큰 갱신/메시지 수신 처리
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 토큰 갱신 시 로그 (백엔드 POST /api/fcm-token 연동은 추후)
        Log.d(TAG, "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // TODO: 실제 알림 표시(NotificationCompat) 구현 — 우선 수신 로그만
        Log.d(
            TAG,
            "onMessageReceived: title=${message.notification?.title}, " +
                "body=${message.notification?.body}, data=${message.data}"
        )
    }

    companion object {
        private const val TAG = "FCM"
    }
}

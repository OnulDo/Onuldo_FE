package com.example.onuldo_fe.service

import android.util.Log
import com.example.onuldo_fe.data.auth.DeviceInfoProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// FCM 수신 서비스 — 토큰 갱신만 처리한다.
// 인앱 알림은 알림함 목록(GET /api/users/me/notifications)에서 노출한다.
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        DeviceInfoProvider.get().saveFcmToken(token)
        Log.d(TAG, "FCM token refreshed")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // 포그라운드 수신 로그만 남김
        Log.d(TAG, "onMessageReceived: title=${message.notification?.title}, data=${message.data}")
    }

    companion object {
        private const val TAG = "FCM"
    }
}

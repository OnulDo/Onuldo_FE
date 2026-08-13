package com.example.onuldo_fe.data.notification

import android.content.Context

/**
 * 회원가입 완료 후 시스템 푸시 권한(POST_NOTIFICATIONS)을 이미 1회 요청했는지 영속 저장한다.
 * 사용자가 거부한 뒤 화면/액티비티가 재생성되어도 다시 요청하지 않도록 하는 용도(기기별 1회)
 */
object NotificationPermissionPrefs {

    private const val PREFS_NAME = "notification_permission" // 알림 권한 요청 여부를 저장할 SharedPreferences 이름
    private const val KEY_REQUESTED = "post_notifications_requested" // 알림 권한을 이미 요청했는지 저장하는 Key

    /** 알림 권한을 이전에 요청했는지 확인한다.
     * 저장된 값이 없으면 아직 요청하지 않은 것으로 판단
     */
    fun isRequested(context: Context): Boolean =
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_REQUESTED, false)

    fun markRequested(context: Context) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_REQUESTED, true)
            .apply()
    }
}

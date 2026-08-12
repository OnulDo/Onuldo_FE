package com.example.onuldo_fe.data.social

import android.content.Context
import android.content.SharedPreferences
import com.example.onuldo_fe.data.auth.dto.SocialProvider

/**
 * 현재 세션이 **어떤 경로로 로그인했는지** 기억한다.
 *
 * 회원 탈퇴 시 소셜 연동까지 끊어야 하는 계정인지 알아야 하는데,
 * 서버 `GET /api/users/me`는 가입 경로를 내려주지 않는다. 그래서 로그인·가입에 성공한 시점에
 * 앱이 직접 남겨 둔다.
 *
 * 자동 로그인으로 세션이 앱 재시작을 넘어 이어지므로 저장은 디스크에 한다.
 * 값이 "KAKAO/EMAIL" 중 하나일 뿐이라 토큰([com.example.onuldo_fe.data.network.PersistentTokenStore])과
 * 달리 암호화하지 않는다.
 */
internal object SocialSessionStore {

    private const val PREFS_NAME = "auth_social_session"
    private const val KEY_PROVIDER = "provider"

    @Volatile
    private var prefs: SharedPreferences? = null

    /** [SocialAuthClient.initialize]에서 앱 시작 시 1회 호출. */
    fun initialize(context: Context) {
        if (prefs == null) synchronized(this) {
            if (prefs == null) {
                prefs = context.applicationContext
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            }
        }
    }

    /**
     * 마지막으로 로그인에 성공한 경로. 기록이 없으면 null.
     *
     * 저장된 이름이 [SocialProvider]에 없으면(앱 다운그레이드 등) null로 본다.
     * 연동 해제를 건너뛸 뿐이라 앱을 죽일 이유가 없다.
     */
    var provider: SocialProvider?
        get() = prefs?.getString(KEY_PROVIDER, null)
            ?.let { name -> runCatching { SocialProvider.valueOf(name) }.getOrNull() }
        set(value) {
            val editor = prefs?.edit() ?: return
            if (value == null) editor.remove(KEY_PROVIDER) else editor.putString(KEY_PROVIDER, value.name)
            editor.apply()
        }
}

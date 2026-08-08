package com.example.onuldo_fe.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * 앱을 재시작해도 유지되는 토큰 저장소. **자동 로그인**의 근거가 된다.
 *
 * ### 왜 DataStore가 아니라 EncryptedSharedPreferences인가
 * [TokenStore]의 `accessToken`/`refreshToken`은 [AuthInterceptor]와 [TokenAuthenticator]가
 * **OkHttp 워커 스레드에서 동기로** 읽는다. DataStore는 Flow/suspend 기반이라 이 자리에서 쓰려면
 * `runBlocking`을 끼워야 하고, 그러면 네트워크 스레드를 붙잡아 ANR 위험이 생긴다.
 * SharedPreferences는 동기 API라 기존 인터페이스를 그대로 만족시킨다.
 *
 * 암호화를 쓰는 이유는 **리프레시 토큰의 수명이 14일**이기 때문이다. 평문으로 두면 기기 백업이나
 * 루팅된 기기에서 유출됐을 때 그 기간 내내 계정을 사용할 수 있다. 키는 Android Keystore가 관리한다.
 *
 * 읽기는 메모리 캐시에서 하고 쓰기만 디스크에 반영한다. 요청마다 복호화가 일어나지 않게 하기 위함이다.
 */
class PersistentTokenStore private constructor(
    private val prefs: SharedPreferences,
) : TokenStore {

    // 디스크 값을 그대로 들고 있는 캐시. 네트워크 스레드에서 읽히므로 @Volatile.
    @Volatile
    private var cachedAccessToken: String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    @Volatile
    private var cachedRefreshToken: String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    override val accessToken: String? get() = cachedAccessToken
    override val refreshToken: String? get() = cachedRefreshToken

    @Synchronized
    override fun update(tokens: AuthTokens) {
        val access = tokens.accessToken.trim().takeIf(String::isNotEmpty)
        val refresh = tokens.refreshToken.trim().takeIf(String::isNotEmpty)

        cachedAccessToken = access
        cachedRefreshToken = refresh

        // apply()는 비동기 디스크 반영이지만 메모리 캐시는 즉시 갱신되므로
        // 바로 뒤따르는 요청도 새 토큰을 쓴다.
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, access)
            .putString(KEY_REFRESH_TOKEN, refresh)
            .apply()
    }

    @Synchronized
    override fun clear() {
        cachedAccessToken = null
        cachedRefreshToken = null
        prefs.edit().clear().apply()
    }

    companion object {
        private const val TAG = "PersistentTokenStore"
        private const val PREFS_NAME = "onuldo_auth_tokens"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"

        /**
         * 암호화 저장소를 연다.
         *
         * 기기 백업 복원이나 Keystore 손상으로 복호화에 실패할 수 있다. 이때 앱이 죽으면 안 되므로
         * **저장본을 버리고 한 번 더 시도**하고, 그래도 실패하면 [InMemoryTokenStore]로 물러난다.
         * 이 경우 자동 로그인만 동작하지 않고 앱은 정상 실행된다.
         */
        fun create(context: Context): TokenStore {
            val appContext = context.applicationContext
            return try {
                PersistentTokenStore(openEncryptedPrefs(appContext))
            } catch (e: Exception) {
                Log.w(TAG, "암호화 저장소 열기 실패 — 저장본을 버리고 재시도", e)
                try {
                    appContext.deleteSharedPreferences(PREFS_NAME)
                    PersistentTokenStore(openEncryptedPrefs(appContext))
                } catch (retry: Exception) {
                    // 자동 로그인을 포기하고 메모리 저장소로 계속 간다.
                    Log.e(TAG, "암호화 저장소 사용 불가 — 메모리 저장소로 대체", retry)
                    InMemoryTokenStore
                }
            }
        }

        private fun openEncryptedPrefs(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }
    }
}

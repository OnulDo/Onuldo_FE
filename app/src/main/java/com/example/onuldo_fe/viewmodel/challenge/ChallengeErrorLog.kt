package com.example.onuldo_fe.viewmodel.challenge

import android.util.Log
import org.json.JSONObject
import retrofit2.HttpException

// 챌린지 ViewModel 공통 에러 로그. Logcat에서 짧은 태그로 검색 (ch_pd 참여 / ch_dt 상세 / ch_ls 목록).
// 서버 에러 응답 바디는 한 번만 읽을 수 있으므로, 여기서 로그와 code 추출을 함께 처리한다.
// 반환값: 서버 에러 code (예: "INSUFFICIENT_POINT_FOR_CHALLENGE"), HTTP 에러가 아니거나 없으면 null.
internal fun logChallengeError(tag: String, e: Throwable): String? {
    if (e !is HttpException) {
        Log.e(tag, "${e.javaClass.simpleName}: ${e.message}", e)
        return null
    }
    val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull().orEmpty()
    val code = runCatching { JSONObject(body).optString("code").takeIf { it.isNotBlank() } }.getOrNull()
    Log.e(tag, "HTTP ${e.code()} $body", e)
    return code
}

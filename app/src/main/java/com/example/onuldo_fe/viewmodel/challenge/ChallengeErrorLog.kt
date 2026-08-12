package com.example.onuldo_fe.viewmodel.challenge

import android.util.Log

// 챌린지 ViewModel 공통 에러 로그. Logcat에서 짧은 태그로 검색 (ch_pd 참여 / ch_dt 상세 / ch_ls 목록).
// 에러 구분/문구 처리는 Repository의 ApiResult가 담당
internal fun logChallengeError(tag: String, code: String, message: String) {
    Log.e(tag, "$code: $message")
}

package com.example.onuldo_fe.data.home.config

/** 홈 기능별 Fake/Real API 전환 설정. */
/** false면 기존 Fake 목록, true면 api 실제 응답을 사용 */
object HomeApiConfig {
    const val USE_REAL_DAILY = true // 오늘 날짜의 챌린지 조회
}

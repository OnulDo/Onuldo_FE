package com.example.onuldo_fe.data.party.config

/** 파티 기능의 Fake/Real 전환 설정. */
object PartyApiConfig {
    /** false면 기존 Fake 목록, true면 GET /api/parties 실제 응답을 사용한다. */
    const val USE_REAL_LIST = false     //나의 파티 목록 조회
    const val USE_REAL_WAITING_ROOM = false // 파티 대기방 조회만 실제 API로 전환
}

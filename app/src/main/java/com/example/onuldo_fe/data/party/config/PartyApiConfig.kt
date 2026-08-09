package com.example.onuldo_fe.data.party.config

/** 파티 기능별 Fake/Real API 전환 설정. */
object PartyApiConfig {
    const val USE_REAL_LIST = true // 나의 파티 목록 조회
    const val USE_REAL_WAITING_ROOM = true // 파티 대기방 조회
    const val USE_REAL_FEED = true // 파티 진행 피드 조회
    const val USE_REAL_CREATE = true // 파티 생성
    const val USE_REAL_READY = true // 파티원 준비 상태 토글
    const val USE_REAL_JOIN = true // 초대코드로 파티 참여
    const val USE_REAL_START = true // 파티 시작
    const val USE_REAL_SETTLEMENT = true // 파티 정산 결과 조회
    const val USE_REAL_LEAVE = true // 파티 대기방 이탈
}

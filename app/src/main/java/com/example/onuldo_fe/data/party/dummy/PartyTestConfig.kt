package com.example.onuldo_fe.data.party.dummy

/**
 * 파티 기능의 fake 테스트 상태를 변경하는 설정
 * 실제 API 연동 시 이 값들은 서버 응답과 로그인 사용자 정보로 교체
 */
object PartyTestConfig {
    /**
     * 파티 생성 직후 대기방 상태 테스트
     * true: 방장 + 준비완료 파티원 1명으로 [시작하기] 활성화 테스트
     * false: 방장만 참여한 모집 상태로 [시작하기] 비활성화 테스트
     */
    const val CREATE_READY_TO_START = true

    /**
     * 현재 사용자의 fake 보유 포인트 테스트
     * 50_000: 일반적인 파티 생성·준비완료 성공 테스트
     * 5_000: 도전금보다 포인트가 부족한 안내 모달 테스트
     */
    const val AVAILABLE_POINT = 50_000
}

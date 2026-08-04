package com.example.onuldo_fe.data.party.dummy

// 초대코드 다이얼로그의 성공 및 오류 상태를 빠르게 확인하기 위한 고정 테스트 값
// 실제 API 연동 후 서버 발급·검증으로 교체하고 해당 값은 제거하거나 테스트 소스로 이동
object PartyInviteDummyData {
    const val VALID_CODE = "82K3H9"                 // 정상 참여 테스트 코드
    const val STARTED_CODE = "START1"               // 이미 시작된 파티 오류 테스트 코드
    const val FULL_CODE = "FULL01"                  // 인원 초과 오류 모달 테스트 코드
    const val EXPIRED_CODE = "OLD123"               // 만료된 초대코드 오류 테스트 코드
}

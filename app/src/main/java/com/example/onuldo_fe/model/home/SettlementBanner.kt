package com.example.onuldo_fe.model.home

// 홈에 노출할 미확인 파티 정산 결과 배너 정보
data class SettlementBanner(
    val partyName: String,                     // 정산이 완료된 파티 이름
    val resultId: String                       // 정산 결과 화면 조회용 식별자
)

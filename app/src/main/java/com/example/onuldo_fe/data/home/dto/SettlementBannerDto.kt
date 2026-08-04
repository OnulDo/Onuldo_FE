package com.example.onuldo_fe.data.home.dto

// 확인하지 않은 파티 정산 결과 배너 응답
data class SettlementBannerDto(
    val partyName: String,               // 정산 완료 파티명
    val partyId: Long,                   // 정산 결과를 조회할 파티 식별자
    val isChecked: Boolean = false       // 정산 결과 확인 여부
)

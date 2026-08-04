package com.example.onuldo_fe.data.party.dto

// 파티 정산 결과 조회 API 응답
data class PartySettlementResultDto(
    val partyId: Long,
    val overallStatus: String,
    val overallTitle: String,
    val overallDescription: String,
    val myResult: PartyMySettlementDto,
    val memberResults: List<PartySettlementMemberDto>
)

// 현재 사용자의 환급금과 보너스 또는 차감 금액
data class PartyMySettlementDto(
    val depositRefundAmount: Int,
    val bonusAmount: Int
)

// 파티원별 완주 여부와 정산 금액 및 프로필 정보
data class PartySettlementMemberDto(
    val userId: Long,
    val name: String,
    val profileImageUrl: String?,
    val defaultCharacterId: Int?,
    val isSuccess: Boolean,
    val bonusAmount: Int
)

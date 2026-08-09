package com.example.onuldo_fe.data.party.dto

/** 파티 정산 결과 조회 API 응답. */
data class PartySettlementResultDto(
    val partyId: Long,                         // 파티 ID
    val name: String,                          // 파티 이름
    val resultType: String,                    // 전체 결과: ALL_SUCCESS / PARTIAL_SUCCESS / ALL_FAIL
    val myDepositAmount: Int,                  // 내가 처음 낸 도전금
    val myDisplayAmount: Int,                  // 내 분배금·보너스(+) 또는 차감액(-)
    val members: List<PartySettlementMemberDto> // 파티원별 정산 결과
)

/** 파티원별 정산 결과. */
data class PartySettlementMemberDto(
    val userId: Long,              // 회원 ID
    val nickname: String,          // 닉네임
    val profileImageUrl: String,   // 회원이 선택한 프로필 이미지 URL
    val status: String,            // ONGOING / SUCCESS / FAIL / CANCELED
    val displayAmount: Int         // 분배금·보너스(+) 또는 차감액(-)
)

package com.example.onuldo_fe.data.party.dummy

import com.example.onuldo_fe.data.party.dto.PartyMySettlementDto
import com.example.onuldo_fe.data.party.dto.PartySettlementMemberDto
import com.example.onuldo_fe.data.party.dto.PartySettlementResultDto

// 실제 정산 API 연결 전 세 가지 결과 화면을 확인하는 Fake 응답
object PartySettlementDummyData {
    const val ALL_SUCCESS_PARTY_ID = 1_001L
    const val PARTIAL_SUCCESS_PARTY_ID = 1_002L
    const val ALL_FAILED_PARTY_ID = 1_003L

    val allSuccess = PartySettlementResultDto(
        partyId = ALL_SUCCESS_PARTY_ID,
        overallStatus = "ALL_SUCCESS",
        overallTitle = "전원 성공!",
        overallDescription = "파티 전원이 챌린지를 완주했어요",
        myResult = PartyMySettlementDto(30_000, 5_000),
        memberResults = members(
            amounts = listOf(5_000, 5_000, 5_000, 5_000, 5_000),
            successCount = 5
        )
    )

    val partialSuccess = PartySettlementResultDto(
        partyId = PARTIAL_SUCCESS_PARTY_ID,
        overallStatus = "PARTIAL_SUCCESS",
        overallTitle = "3명이 완주했어요",
        overallDescription = "미완주 파티원의 도전금이 완주자에게 배분됐어요",
        myResult = PartyMySettlementDto(30_000, 10_000),
        memberResults = members(
            amounts = listOf(10_000, 10_000, 10_000, -12_000, -18_000),
            successCount = 3
        )
    )

    val allFailed = PartySettlementResultDto(
        partyId = ALL_FAILED_PARTY_ID,
        overallStatus = "ALL_FAILED",
        overallTitle = "아쉽게 실패했어요",
        overallDescription = "이번엔 아무도 목표를 채우지 못했어요",
        myResult = PartyMySettlementDto(18_000, -12_000),
        memberResults = members(
            amounts = listOf(-12_000, -9_000, -15_000, -18_000, -10_500),
            successCount = 0
        )
    )

    fun get(partyId: Long): PartySettlementResultDto = when (partyId) {
        PARTIAL_SUCCESS_PARTY_ID -> partialSuccess
        ALL_FAILED_PARTY_ID -> allFailed
        else -> allSuccess.copy(partyId = partyId)
    }

    private fun members(amounts: List<Int>, successCount: Int): List<PartySettlementMemberDto> {
        val names = listOf("오늘두", "지호", "수아", "도윤", "하늘")
        val characterIds = listOf(4, 5, 3, 8, 9)
        return names.indices.map { index ->
            PartySettlementMemberDto(
                memberId = "settlement-member-${index + 1}",
                name = names[index],
                profileImageUrl = null,
                defaultCharacterId = characterIds[index],
                isSuccess = index < successCount,
                bonusAmount = amounts[index]
            )
        }
    }
}

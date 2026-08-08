package com.example.onuldo_fe.data.party.dummy

import com.example.onuldo_fe.data.party.dto.PartySettlementMemberDto
import com.example.onuldo_fe.data.party.dto.PartySettlementResultDto

// Preview와 Fake 모드에서 Swagger 응답 구조를 그대로 재현한다.
object PartySettlementDummyData {
    const val ALL_SUCCESS_PARTY_ID = 1_001L
    const val PARTIAL_SUCCESS_PARTY_ID = 1_002L
    const val ALL_FAIL_PARTY_ID = 1_003L

    val allSuccess = PartySettlementResultDto(
        partyId = ALL_SUCCESS_PARTY_ID,
        name = "전원 완주 파티",
        resultType = "ALL_SUCCESS",
        myDepositAmount = 30_000,
        myDisplayAmount = 5_000,
        members = members(
            amounts = listOf(5_000, 5_000, 5_000, 5_000, 5_000),
            statuses = List(5) { "SUCCESS" }
        )
    )

    val partialSuccess = PartySettlementResultDto(
        partyId = PARTIAL_SUCCESS_PARTY_ID,
        name = "부지런 파티",
        resultType = "PARTIAL_SUCCESS",
        myDepositAmount = 30_000,
        myDisplayAmount = 10_000,
        members = members(
            amounts = listOf(10_000, 10_000, 10_000, -12_000, -18_000),
            statuses = listOf("SUCCESS", "SUCCESS", "SUCCESS", "FAIL", "CANCELED")
        )
    )

    val allFailed = PartySettlementResultDto(
        partyId = ALL_FAIL_PARTY_ID,
        name = "다시 도전 파티",
        resultType = "ALL_FAIL",
        myDepositAmount = 18_000,
        myDisplayAmount = -12_000,
        members = members(
            amounts = listOf(-12_000, -9_000, -15_000, -18_000, -10_500),
            statuses = List(5) { "FAIL" }
        )
    )

    fun get(partyId: Long): PartySettlementResultDto = when (partyId) {
        PARTIAL_SUCCESS_PARTY_ID -> partialSuccess
        ALL_FAIL_PARTY_ID -> allFailed
        else -> allSuccess.copy(partyId = partyId)
    }

    private fun members(amounts: List<Int>, statuses: List<String>): List<PartySettlementMemberDto> {
        val names = listOf("오늘두", "지호", "수아", "도윤", "하늘")
        return names.indices.map { index ->
            PartySettlementMemberDto(
                userId = (index + 1).toLong(),
                nickname = names[index],
                profileImageUrl = "https://example.com/profiles/${index + 1}.png",
                status = statuses[index],
                displayAmount = amounts[index]
            )
        }
    }
}

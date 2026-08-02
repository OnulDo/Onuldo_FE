package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.party.api.FakePartyApi
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.RealPartySummaryDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class PartyRepositoryImplTest {
    @Test
    fun `설정이 false이면 기존 Fake 목록을 사용한다`() = runBlocking {
        val repository = PartyRepositoryImpl(
            fakeApi = FakePartyApi(),
            realApi = ThrowingRealPartyApi,
            useRealPartyListApi = false
        )

        val parties = repository.getParties()

        assertEquals("새벽 러너 파티", parties.first().partyName)
    }

    @Test
    fun `설정이 true이면 실제 API 응답 형식을 도메인 모델로 변환한다`() = runBlocking {
        val repository = PartyRepositoryImpl(
            fakeApi = FakePartyApi(),
            realApi = SuccessfulRealPartyApi,
            useRealPartyListApi = true
        )

        val party = repository.getParties().single()

        assertEquals("101", party.partyId)
        assertEquals("30일 헬스 챌린지 파티", party.partyName)
        assertEquals("", party.challengeName)
        assertEquals(3, party.completedMemberCount)
        assertEquals(4, party.totalMemberCount)
    }

    private object ThrowingRealPartyApi : RealPartyApi {
        override suspend fun getParties(): Response<ApiResponse<List<RealPartySummaryDto>>> =
            error("Fake 모드에서 실제 API가 호출되면 안 됩니다.")
    }

    private object SuccessfulRealPartyApi : RealPartyApi {
        override suspend fun getParties(): Response<ApiResponse<List<RealPartySummaryDto>>> =
            Response.success(
                ApiResponse(
                    timestamp = "2026-07-23T13:00:00",
                    code = "SUCCESS",
                    message = "요청에 성공하였습니다.",
                    result = listOf(
                        RealPartySummaryDto(
                            partyId = 101,
                            name = "30일 헬스 챌린지 파티",
                            status = "ONGOING",
                            dDay = 12,
                            progressRate = 0.72,
                            verifiedToday = 3,
                            totalMembers = 4
                        )
                    )
                )
            )
    }
}

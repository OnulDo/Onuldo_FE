package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto
import com.example.onuldo_fe.data.party.dto.PartyHomeResultDto
import com.example.onuldo_fe.data.party.dto.RealPartyWaitingRoomDto
import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import com.example.onuldo_fe.data.party.dto.PartyJoinRequestDto
import com.example.onuldo_fe.data.party.dto.PartyReadinessRequestDto
import com.example.onuldo_fe.data.party.dto.PartyStartResponseDto
import com.example.onuldo_fe.data.party.dto.PartyListPageResponseDto
import com.example.onuldo_fe.data.party.dto.PartySettlementResultDto
import com.example.onuldo_fe.data.party.dto.PartyLeaveResponseDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class PartyFeedRepositoryImplTest {
    @Test
    fun `피드 설정이 true이면 실제 응답을 도메인 모델로 변환한다`() = runBlocking {
        val repository = PartyFeedRepositoryImpl(SuccessfulRealFeedApi)

        val feed = repository.getPartyFeed("101")

        assertEquals("갓생팟", feed.partyName)
        assertEquals("새벽 6시 기상", feed.challengeName)
        assertEquals(0.72, feed.progress.progressRate, 0.0)
        assertEquals(3, feed.progress.completedMemberCount)
        assertEquals(true, feed.items.single().isVerifiedToday)
    }

    private object SuccessfulRealFeedApi : RealPartyApi {
        override suspend fun startParty(partyId: Long): Response<ApiResponse<PartyStartResponseDto>> =
            error("피드 테스트에서 시작 API가 호출되면 안 됩니다.")
        override suspend fun joinParty(request: PartyJoinRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("피드 테스트에서 참여 API가 호출되면 안 됩니다.")
        override suspend fun createParty(request: CreatePartyRequestDto): Response<ApiResponse<CreatePartyResponseDto>> =
            error("피드 테스트에서 생성 API가 호출되면 안 됩니다.")

        override suspend fun getParties(cursor: String?, size: Int): Response<PartyListPageResponseDto> =
            error("피드 테스트에서 목록 API가 호출되면 안 됩니다.")

        override suspend fun getHomeParties(): Response<ApiResponse<PartyHomeResultDto>> =
            error("피드 테스트에서 홈 파티 API가 호출되면 안 됩니다.")

        override suspend fun getWaitingRoom(partyId: Long): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("피드 테스트에서 대기방 API가 호출되면 안 됩니다.")

        override suspend fun readyParty(partyId: Long, request: PartyReadinessRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("피드 테스트에서 준비 API가 호출되면 안 됩니다.")

        override suspend fun leaveParty(partyId: Long): Response<ApiResponse<PartyLeaveResponseDto>> =
            error("피드 테스트에서 이탈 API가 호출되면 안 됩니다.")

        override suspend fun getPartyFeed(partyId: Long): Response<ApiResponse<PartyFeedDto>> =
            Response.success(
                ApiResponse(
                    timestamp = "2026-07-23T13:00:00",
                    code = "SUCCESS",
                    message = "요청에 성공하였습니다.",
                    result = PartyFeedDto(
                        partyId = partyId,
                        name = "갓생팟",
                        challengeTitle = "새벽 6시 기상",
                        progressRate = 0.72,
                        verifiedMemberCount = 3,
                        totalMemberCount = 4,
                        members = listOf(
                            PartyFeedItemDto(
                                userId = 5,
                                nickname = "민지",
                                profileImageUrl = "https://cdn.onuldo.com/profile/5.png",
                                isVerifiedToday = true,
                                verificationPhotoUrl = "https://cdn.onuldo.com/verification/101.png",
                                verifiedAt = "2026-07-23T09:00:00"
                            )
                        )
                    )
                )
            )

        override suspend fun getSettlementResult(partyId: Long): Response<ApiResponse<PartySettlementResultDto>> =
            error("피드 테스트에서 정산 API가 호출되면 안 됩니다.")
    }
}

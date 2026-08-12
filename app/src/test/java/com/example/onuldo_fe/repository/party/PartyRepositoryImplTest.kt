package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.party.api.FakePartyApi
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.RealPartySummaryDto
import com.example.onuldo_fe.data.party.dto.RealPartyMemberDto
import com.example.onuldo_fe.data.party.dto.RealPartyWaitingRoomDto
import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.data.party.dto.PartyHomeResultDto
import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import com.example.onuldo_fe.data.party.dto.PartyJoinRequestDto
import com.example.onuldo_fe.data.party.dto.PartyStartResponseDto
import com.example.onuldo_fe.data.party.dto.PartyListPageResponseDto
import com.example.onuldo_fe.data.party.dto.PartyListMemberDto
import com.example.onuldo_fe.data.party.dto.PartyLeaveResponseDto
import com.example.onuldo_fe.data.party.dto.PartyReadinessRequestDto
import com.example.onuldo_fe.data.party.dto.PartySettlementMemberDto
import com.example.onuldo_fe.data.party.dto.PartySettlementResultDto
import com.example.onuldo_fe.model.party.CreatePartyCommand
import com.example.onuldo_fe.model.party.PartyLifecycleStatus
import com.example.onuldo_fe.model.party.PartySettlementMemberStatus
import com.example.onuldo_fe.model.party.PartySettlementStatus
import com.example.onuldo_fe.model.party.PartyVerificationStatus
import com.google.gson.JsonPrimitive
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
        assertEquals("30일 헬스", party.challengeName)
        assertEquals("매일 30분 운동", party.goal)
        assertEquals("D-12", party.dDay)
        assertEquals("21:00:00", party.deadline)
        assertEquals(3, party.completedMemberCount)
        assertEquals(4, party.totalMemberCount)
        assertEquals(PartyVerificationStatus.Success, party.verificationStatus)
        assertEquals(PartyLifecycleStatus.Disbanded, party.status)
        assertEquals("민지", party.members.first().nickname)
        assertEquals("https://example.com/profiles/1.png", party.members.first().profileImageUrl)
        assertEquals(true, party.members.first().isVerifiedToday)
    }

    @Test
    fun `대기방 설정이 true이면 서버 권한과 시작 가능 여부를 전달한다`() = runBlocking {
        val repository = PartyRepositoryImpl(
            fakeApi = FakePartyApi(),
            realApi = SuccessfulWaitingRoomApi,
            useRealPartyListApi = false,
            useRealPartyWaitingRoomApi = true
        )

        val room = repository.getWaitingRoom("101")

        assertEquals("101", room.partyId)
        assertEquals("갓생팟", room.partyName)
        assertEquals(true, room.isHost)
        assertEquals(false, room.canStart)
        assertEquals(2, room.members.size)
    }

    @Test
    fun `생성 설정이 true이면 POST 요청값을 변환하고 생성 결과를 전달한다`() = runBlocking {
        val repository = PartyRepositoryImpl(
            fakeApi = FakePartyApi(),
            realApi = SuccessfulCreateRealApi,
            useRealPartyListApi = false,
            useRealPartyCreateApi = true
        )

        val created = repository.createParty(
            CreatePartyCommand("갓생팟", "12", "새벽 6시 기상", "4주", 30_000, 4)
        )

        assertEquals("101", created.partyId)
        assertEquals("82K3H9", created.inviteCode)
    }

    @Test
    fun `준비 설정이 true이면 POST 성공 응답의 최신 대기방을 전달한다`() = runBlocking {
        val repository = PartyRepositoryImpl(
            fakeApi = FakePartyApi(),
            realApi = SuccessfulReadyRealApi,
            useRealPartyListApi = false,
            useRealPartyReadyApi = true
        )

        val room = repository.readyParty("101", ready = true)

        assertEquals("101", room.partyId)
        assertEquals("READY", room.members.last().readyStatus.name.uppercase())
        assertEquals(true, room.canStart)
    }

    @Test
    fun `시작 설정이 true이면 실제 POST 성공 응답을 완료 처리한다`() = runBlocking {
        val repository = PartyRepositoryImpl(
            fakeApi = FakePartyApi(),
            realApi = SuccessfulReadyRealApi,
            useRealPartyListApi = false,
            useRealPartyStartApi = true
        )

        repository.startParty("101")
    }

    @Test
    fun `이탈 설정이 true이면 실제 POST 성공 응답을 완료 처리한다`() = runBlocking {
        // 다른 테스트가 먼저 실행돼 남아있을 수 있는 호출 기록을 초기화한다.
        SuccessfulReadyRealApi.leaveCalledWithPartyId = null
        val repository = PartyRepositoryImpl(
            fakeApi = FakePartyApi(),
            realApi = SuccessfulReadyRealApi,
            useRealPartyListApi = false,
            useRealPartyLeaveApi = true
        )

        repository.leaveParty("101")

        assertEquals(101L, SuccessfulReadyRealApi.leaveCalledWithPartyId)
    }

    @Test
    fun `정산 설정이 true이면 실제 응답의 금액과 파티원 상태를 변환한다`() = runBlocking {
        val repository = PartyRepositoryImpl(
            fakeApi = FakePartyApi(),
            realApi = SuccessfulReadyRealApi,
            useRealPartyListApi = false,
            useRealPartySettlementApi = true
        )

        val result = repository.getSettlementResult(101)

        assertEquals(PartySettlementStatus.PartialSuccess, result.status)
        assertEquals("부지런 파티", result.partyName)
        assertEquals(30_000, result.depositAmount)
        assertEquals(-12_000, result.displayAmount)
        assertEquals("1명이 완주했어요", result.title)
        assertEquals(1, result.completedMemberCount)
        assertEquals(
            listOf(
                PartySettlementMemberStatus.Ongoing,
                PartySettlementMemberStatus.Success,
                PartySettlementMemberStatus.Fail,
                PartySettlementMemberStatus.Canceled
            ),
            result.members.map { it.status }
        )
        assertEquals(
            listOf(0, 5_000, -12_000, 0),
            result.members.map { it.displayAmount }
        )
    }

    private object ThrowingRealPartyApi : RealPartyApi {
        override suspend fun startParty(partyId: Long): Response<ApiResponse<PartyStartResponseDto>> = error("테스트에서 시작 API가 호출되면 안 됩니다.")
        override suspend fun joinParty(request: PartyJoinRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> = error("테스트에서 참여 API가 호출되면 안 됩니다.")
        override suspend fun createParty(request: CreatePartyRequestDto): Response<ApiResponse<CreatePartyResponseDto>> =
            error("Fake 모드에서 실제 생성 API가 호출되면 안 됩니다.")

        override suspend fun getParties(cursor: String?, size: Int): Response<PartyListPageResponseDto> =
            error("Fake 모드에서 실제 API가 호출되면 안 됩니다.")

        override suspend fun getHomeParties(): Response<ApiResponse<PartyHomeResultDto>> =
            error("파티 Repository 테스트에서 홈 파티 API가 호출되면 안 됩니다.")

        override suspend fun getWaitingRoom(partyId: Long): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("Fake 모드에서 실제 대기방 API가 호출되면 안 됩니다.")

        override suspend fun readyParty(partyId: Long, request: PartyReadinessRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("Fake 모드에서 실제 준비 API가 호출되면 안 됩니다.")

        override suspend fun leaveParty(partyId: Long): Response<ApiResponse<PartyLeaveResponseDto>> =
            error("Fake 모드에서 실제 이탈 API가 호출되면 안 됩니다.")

        override suspend fun getPartyFeed(partyId: Long): Response<ApiResponse<PartyFeedDto>> =
            error("파티 Repository 테스트에서 피드 API가 호출되면 안 됩니다.")

        override suspend fun getSettlementResult(partyId: Long): Response<ApiResponse<PartySettlementResultDto>> =
            error("Fake 모드에서 실제 정산 API가 호출되면 안 됩니다.")
    }

    private object SuccessfulRealPartyApi : RealPartyApi {
        override suspend fun startParty(partyId: Long): Response<ApiResponse<PartyStartResponseDto>> = error("목록 테스트에서 시작 API가 호출되면 안 됩니다.")
        override suspend fun joinParty(request: PartyJoinRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> = error("목록 테스트에서 참여 API가 호출되면 안 됩니다.")
        override suspend fun createParty(request: CreatePartyRequestDto): Response<ApiResponse<CreatePartyResponseDto>> =
            error("목록 테스트에서 생성 API가 호출되면 안 됩니다.")

        override suspend fun getParties(cursor: String?, size: Int): Response<PartyListPageResponseDto> =
            Response.success(
                PartyListPageResponseDto(
                    timestamp = "2026-07-23T13:00:00",
                    code = "SUCCESS",
                    message = "요청에 성공하였습니다.",
                    result = listOf(
                        RealPartySummaryDto(
                            partyId = 101,
                            name = "30일 헬스 챌린지 파티",
                            challengeTitle = "30일 헬스",
                            goal = "매일 30분 운동",
                            status = "DISSOLVED",
                            myStatus = "SUCCESS",
                            endDate = "2026-08-20",
                            dDay = 12,
                            verificationDeadline = JsonPrimitive("21:00:00"),
                            progressRate = 0.72,
                            verifiedMemberCount = 3,
                            totalMemberCount = 4,
                            members = listOf(
                                PartyListMemberDto(1, "민지", "https://example.com/profiles/1.png", true),
                                PartyListMemberDto(2, "지호", "https://example.com/profiles/2.png", true),
                                PartyListMemberDto(3, "수아", "https://example.com/profiles/3.png", true),
                                PartyListMemberDto(4, "도윤", "https://example.com/profiles/4.png", false)
                            )
                        )
                    ),
                    nextCursor = null,
                    hasNext = false
                )
            )

        override suspend fun getHomeParties(): Response<ApiResponse<PartyHomeResultDto>> =
            error("목록 테스트에서 홈 파티 API가 호출되면 안 됩니다.")

        override suspend fun getWaitingRoom(partyId: Long): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("목록 테스트에서 실제 대기방 API가 호출되면 안 됩니다.")

        override suspend fun readyParty(partyId: Long, request: PartyReadinessRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("목록 테스트에서 준비 API가 호출되면 안 됩니다.")

        override suspend fun leaveParty(partyId: Long): Response<ApiResponse<PartyLeaveResponseDto>> =
            error("목록 테스트에서 이탈 API가 호출되면 안 됩니다.")

        override suspend fun getPartyFeed(partyId: Long): Response<ApiResponse<PartyFeedDto>> =
            error("목록 테스트에서 피드 API가 호출되면 안 됩니다.")

        override suspend fun getSettlementResult(partyId: Long): Response<ApiResponse<PartySettlementResultDto>> =
            error("목록 테스트에서 정산 API가 호출되면 안 됩니다.")
    }

    private object SuccessfulWaitingRoomApi : RealPartyApi {
        override suspend fun startParty(partyId: Long): Response<ApiResponse<PartyStartResponseDto>> = error("대기방 테스트에서 시작 API가 호출되면 안 됩니다.")
        override suspend fun joinParty(request: PartyJoinRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> = error("대기방 테스트에서 참여 API가 호출되면 안 됩니다.")
        override suspend fun createParty(request: CreatePartyRequestDto): Response<ApiResponse<CreatePartyResponseDto>> =
            error("대기방 테스트에서 생성 API가 호출되면 안 됩니다.")

        override suspend fun getParties(cursor: String?, size: Int): Response<PartyListPageResponseDto> =
            error("대기방 테스트에서 목록 API가 호출되면 안 됩니다.")

        override suspend fun getHomeParties(): Response<ApiResponse<PartyHomeResultDto>> =
            error("대기방 테스트에서 홈 파티 API가 호출되면 안 됩니다.")

        override suspend fun getWaitingRoom(partyId: Long): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            Response.success(
                ApiResponse(
                    timestamp = "2026-07-23T13:00:00",
                    code = "SUCCESS",
                    message = "요청에 성공하였습니다.",
                    result = RealPartyWaitingRoomDto(
                        partyId = partyId,
                        name = "갓생팟",
                        status = "WAITING",
                        inviteCode = "82K3H9",
                        currentMembers = 2,
                        maxMembers = 4,
                        durationDays = 28,
                        depositAmount = 30_000,
                        members = listOf(
                            RealPartyMemberDto(1, "방장", null, "HOST", "WAITING"),
                            RealPartyMemberDto(2, "파티원", null, "MEMBER", "READY")
                        ),
                        isHost = true,
                        canStart = false
                    )
                )
            )

        override suspend fun getPartyFeed(partyId: Long): Response<ApiResponse<PartyFeedDto>> =
            error("대기방 테스트에서 피드 API가 호출되면 안 됩니다.")

        override suspend fun readyParty(partyId: Long, request: PartyReadinessRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("대기방 테스트에서 준비 API가 호출되면 안 됩니다.")

        override suspend fun leaveParty(partyId: Long): Response<ApiResponse<PartyLeaveResponseDto>> =
            error("대기방 테스트에서 이탈 API가 호출되면 안 됩니다.")

        override suspend fun getSettlementResult(partyId: Long): Response<ApiResponse<PartySettlementResultDto>> =
            error("대기방 테스트에서 정산 API가 호출되면 안 됩니다.")
    }

    private object SuccessfulCreateRealApi : RealPartyApi {
        override suspend fun startParty(partyId: Long): Response<ApiResponse<PartyStartResponseDto>> = error("생성 테스트에서 시작 API가 호출되면 안 됩니다.")
        override suspend fun joinParty(request: PartyJoinRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> = error("생성 테스트에서 참여 API가 호출되면 안 됩니다.")
        override suspend fun createParty(request: CreatePartyRequestDto): Response<ApiResponse<CreatePartyResponseDto>> {
            assertEquals("갓생팟", request.name)
            assertEquals(12L, request.challengeId)
            assertEquals(4, request.durationWeeks)
            assertEquals(30_000, request.depositAmount)
            assertEquals(4, request.maxMembers)
            return Response.success(
                ApiResponse(
                    timestamp = "2026-07-23T13:00:00",
                    code = "SUCCESS",
                    message = "요청에 성공하였습니다.",
                    result = CreatePartyResponseDto(
                        partyId = 101,
                        name = request.name,
                        inviteCode = "82K3H9",
                        inviteExpiresAt = "2026-08-20T00:00:00",
                        status = "WAITING",
                        hostUserId = 5,
                        maxMembers = request.maxMembers,
                        createdAt = "2026-07-23T13:00:00"
                    )
                )
            )
        }

        override suspend fun getParties(cursor: String?, size: Int): Response<PartyListPageResponseDto> =
            error("생성 테스트에서 목록 API가 호출되면 안 됩니다.")

        override suspend fun getHomeParties(): Response<ApiResponse<PartyHomeResultDto>> =
            error("생성 테스트에서 홈 파티 API가 호출되면 안 됩니다.")

        override suspend fun getWaitingRoom(partyId: Long): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("생성 테스트에서 대기방 API가 호출되면 안 됩니다.")

        override suspend fun readyParty(partyId: Long, request: PartyReadinessRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("생성 테스트에서 준비 API가 호출되면 안 됩니다.")

        override suspend fun leaveParty(partyId: Long): Response<ApiResponse<PartyLeaveResponseDto>> =
            error("생성 테스트에서 이탈 API가 호출되면 안 됩니다.")

        override suspend fun getPartyFeed(partyId: Long): Response<ApiResponse<PartyFeedDto>> =
            error("생성 테스트에서 피드 API가 호출되면 안 됩니다.")

        override suspend fun getSettlementResult(partyId: Long): Response<ApiResponse<PartySettlementResultDto>> =
            error("생성 테스트에서 정산 API가 호출되면 안 됩니다.")
    }

    private object SuccessfulReadyRealApi : RealPartyApi {
        // 이탈 API가 실제로 호출됐는지, 어떤 partyId로 호출됐는지 테스트에서 확인하기 위한 기록.
        var leaveCalledWithPartyId: Long? = null

        override suspend fun startParty(partyId: Long): Response<ApiResponse<PartyStartResponseDto>> =
            Response.success(
                ApiResponse(
                    timestamp = "2026-07-23T13:00:00",
                    code = "SUCCESS",
                    message = "요청에 성공하였습니다.",
                    result = PartyStartResponseDto(partyId, "ONGOING", "2026-07-23T13:00:00")
                )
            )
        override suspend fun joinParty(request: PartyJoinRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> = error("준비 테스트에서 참여 API가 호출되면 안 됩니다.")
        override suspend fun readyParty(partyId: Long, request: PartyReadinessRequestDto): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            Response.success(
                ApiResponse(
                    timestamp = "2026-07-23T13:00:00",
                    code = "SUCCESS",
                    message = "요청에 성공하였습니다.",
                    result = RealPartyWaitingRoomDto(
                        partyId = partyId,
                        name = "갓생팟",
                        status = "WAITING",
                        inviteCode = "82K3H9",
                        currentMembers = 2,
                        maxMembers = 4,
                        durationDays = 28,
                        depositAmount = 30_000,
                        members = listOf(
                            RealPartyMemberDto(5, "방장", null, "HOST", "WAITING"),
                            RealPartyMemberDto(7, "이서연", null, "MEMBER", "READY")
                        ),
                        isHost = false,
                        canStart = true
                    )
                )
            )

        override suspend fun createParty(request: CreatePartyRequestDto): Response<ApiResponse<CreatePartyResponseDto>> =
            error("준비 테스트에서 생성 API가 호출되면 안 됩니다.")

        override suspend fun getParties(cursor: String?, size: Int): Response<PartyListPageResponseDto> =
            error("준비 테스트에서 목록 API가 호출되면 안 됩니다.")

        override suspend fun getHomeParties(): Response<ApiResponse<PartyHomeResultDto>> =
            error("준비 테스트에서 홈 파티 API가 호출되면 안 됩니다.")

        override suspend fun getWaitingRoom(partyId: Long): Response<ApiResponse<RealPartyWaitingRoomDto>> =
            error("준비 테스트에서 대기방 API가 호출되면 안 됩니다.")

        override suspend fun getPartyFeed(partyId: Long): Response<ApiResponse<PartyFeedDto>> =
            error("준비 테스트에서 피드 API가 호출되면 안 됩니다.")

        override suspend fun getSettlementResult(partyId: Long): Response<ApiResponse<PartySettlementResultDto>> =
            Response.success(
                ApiResponse(
                    timestamp = "2026-07-23T13:00:00",
                    code = "SUCCESS",
                    message = "요청에 성공하였습니다.",
                    result = PartySettlementResultDto(
                        partyId = partyId,
                        name = "부지런 파티",
                        resultType = "PARTIAL_SUCCESS",
                        myDepositAmount = 30_000,
                        myDisplayAmount = -12_000,
                        members = listOf(
                            PartySettlementMemberDto(1, "민지", "https://example.com/1.png", "ONGOING", 0),
                            PartySettlementMemberDto(2, "지호", "https://example.com/2.png", "SUCCESS", 5_000),
                            PartySettlementMemberDto(3, "수아", "https://example.com/3.png", "FAIL", -12_000),
                            PartySettlementMemberDto(4, "도윤", "https://example.com/4.png", "CANCELED", 0)
                        )
                    )
                )
            )

        override suspend fun leaveParty(partyId: Long): Response<ApiResponse<PartyLeaveResponseDto>> {
            leaveCalledWithPartyId = partyId
            return Response.success(
                ApiResponse(
                    timestamp = "2026-07-23T13:00:00",
                    code = "SUCCESS",
                    message = "요청에 성공하였습니다.",
                    result = PartyLeaveResponseDto(
                        partyId = partyId,
                        dissolved = false,
                        newHostUserId = 7
                    )
                )
            )
        }
    }
}

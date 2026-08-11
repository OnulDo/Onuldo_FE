package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.party.dto.RealPartyWaitingRoomDto
import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import com.example.onuldo_fe.data.party.dto.PartyJoinRequestDto
import com.example.onuldo_fe.data.party.dto.PartyHomeResultDto
import com.example.onuldo_fe.data.party.dto.PartyStartResponseDto
import com.example.onuldo_fe.data.party.dto.PartyListPageResponseDto
import com.example.onuldo_fe.data.party.dto.PartySettlementResultDto
import com.example.onuldo_fe.data.party.dto.PartyLeaveResponseDto
import com.example.onuldo_fe.data.party.dto.PartyReadinessRequestDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface RealPartyApi {

    /** 입력값을 JSON Body로 전송해 로그인 사용자를 방장으로 하는 파티를 생성한다. */
    @POST("api/parties")
    suspend fun createParty(
        @Body request: CreatePartyRequestDto
    ): Response<ApiResponse<CreatePartyResponseDto>>

    /** 초대코드를 JSON Body로 전송해 파티에 참여하고 최신 대기방을 반환한다. */
    @POST("api/parties/members")
    suspend fun joinParty(
        @Body request: PartyJoinRequestDto
    ): Response<ApiResponse<RealPartyWaitingRoomDto>>

    /** 커서 기반으로 나의 파티 목록 첫 페이지 또는 다음 페이지를 조회한다. */
    @GET("api/parties")
    suspend fun getParties(
        @Query("cursor") cursor: String? = null,
        @Query("size") size: Int = 10
    ): Response<PartyListPageResponseDto>

    /** 홈 화면에 표시할 진행 중 파티와 미확인 정산 배너를 조회한다. */
    @GET("api/parties/home")
    suspend fun getHomeParties(): Response<ApiResponse<PartyHomeResultDto>>

    /** 로그인 사용자가 참여 중인 파티의 대기방 상태를 조회 */
    @GET("api/parties/{partyId}/waiting-room")
    suspend fun getWaitingRoom(
        @Path("partyId") partyId: Long
    ): Response<ApiResponse<RealPartyWaitingRoomDto>>

    /** 로그인 파티원의 준비 상태를 지정하고 최신 대기방 상태를 반환한다. */
    @PATCH("api/parties/{partyId}/members/me/readiness")
    suspend fun readyParty(
        @Path("partyId") partyId: Long,
        @Body request: PartyReadinessRequestDto
    ): Response<ApiResponse<RealPartyWaitingRoomDto>>

    /** 대기 중인 파티에서 이탈하고 해체·방장 승계 결과를 받는다. */
    @DELETE("api/parties/{partyId}/members/me")
    suspend fun leaveParty(
        @Path("partyId") partyId: Long
    ): Response<ApiResponse<PartyLeaveResponseDto>>

    /** 방장이 파티를 시작해 상태 전환과 전원의 도전금 차감을 요청한다. */
    @PATCH("api/parties/{partyId}/status")
    suspend fun startParty(
        @Path("partyId") partyId: Long
    ): Response<ApiResponse<PartyStartResponseDto>>

    /** 파티의 오늘 인증 진행률과 파티원별 인증 현황을 조회한다. */
    @GET("api/parties/{partyId}/feed")
    suspend fun getPartyFeed(
        @Path("partyId") partyId: Long
    ): Response<ApiResponse<PartyFeedDto>>

    /** 파티 정산 결과를 조회하고 해당 결과 배너를 확인 처리한다. */
    @GET("api/parties/{partyId}/results")
    suspend fun getSettlementResult(
        @Path("partyId") partyId: Long
    ): Response<ApiResponse<PartySettlementResultDto>>
}

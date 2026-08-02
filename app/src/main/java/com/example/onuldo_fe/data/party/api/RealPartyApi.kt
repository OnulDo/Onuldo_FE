package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.party.dto.RealPartySummaryDto
import com.example.onuldo_fe.data.party.dto.RealPartyWaitingRoomDto
import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST

interface RealPartyApi {

    /** 입력값을 JSON Body로 전송해 로그인 사용자를 방장으로 하는 파티를 생성한다. */
    @POST("api/parties")
    suspend fun createParty(
        @Body request: CreatePartyRequestDto
    ): Response<ApiResponse<CreatePartyResponseDto>>

    /** 나의 파티 목록 조회 */
    @GET("api/parties")
    suspend fun getParties(): Response<ApiResponse<List<RealPartySummaryDto>>>

    /** 로그인 사용자가 참여 중인 파티의 대기방 상태를 조회 */
    @GET("api/parties/{partyId}/waiting")
    suspend fun getWaitingRoom(
        @Path("partyId") partyId: Long
    ): Response<ApiResponse<RealPartyWaitingRoomDto>>

    /** 파티의 오늘 인증 진행률과 파티원별 인증 현황을 조회한다. */
    @GET("api/parties/{partyId}/feed")
    suspend fun getPartyFeed(
        @Path("partyId") partyId: Long
    ): Response<ApiResponse<PartyFeedDto>>
}

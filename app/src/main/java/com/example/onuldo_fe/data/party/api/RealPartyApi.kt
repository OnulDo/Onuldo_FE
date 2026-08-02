package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.party.dto.RealPartySummaryDto
import com.example.onuldo_fe.data.party.dto.RealPartyWaitingRoomDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RealPartyApi {

    /** 나의 파티 목록 조회 */
    @GET("api/parties")
    suspend fun getParties(): Response<ApiResponse<List<RealPartySummaryDto>>>

    /** 로그인 사용자가 참여 중인 파티의 대기방 상태를 조회 */
    @GET("api/parties/{partyId}/waiting")
    suspend fun getWaitingRoom(
        @Path("partyId") partyId: Long
    ): Response<ApiResponse<RealPartyWaitingRoomDto>>
}

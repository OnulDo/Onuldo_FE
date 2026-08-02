package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.party.dto.RealPartySummaryDto
import retrofit2.Response
import retrofit2.http.GET

interface RealPartyApi {
    @GET("api/parties")
    suspend fun getParties(): Response<ApiResponse<List<RealPartySummaryDto>>>
}

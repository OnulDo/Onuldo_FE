package com.example.onuldo_fe.data.record.api

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.record.dto.CompletedRecordResponseDto
import com.example.onuldo_fe.data.record.dto.OngoingChallengeDto
import retrofit2.http.GET

interface RecordApi {
    @GET("api/users/me/challenges/records/ongoing")
    suspend fun getOngoingChallenges(): ApiResponse<List<OngoingChallengeDto>>

    @GET("api/users/me/challenges/records/completed")
    suspend fun getCompletedChallenges(): ApiResponse<CompletedRecordResponseDto>
}
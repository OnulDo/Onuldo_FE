package com.example.onuldo_fe.data.home.api

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.home.dto.RealHomeDailyResultDto
import retrofit2.Response
import retrofit2.http.GET

interface RealHomeApi {
    /** 로그인 사용자가 오늘 수행할 개인·파티 챌린지를 조회한다. */
    @GET("api/users/me/challenges/daily")
    suspend fun getDailyChallenges(): Response<ApiResponse<RealHomeDailyResultDto>>
}

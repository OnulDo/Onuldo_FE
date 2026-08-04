package com.example.onuldo_fe.data.challenge.api

import com.example.onuldo_fe.data.challenge.dto.ChallengeItemDto
import com.example.onuldo_fe.data.challenge.dto.ChallengeListResultDto
import com.example.onuldo_fe.data.challenge.dto.DailyChallengeResultDto
import com.example.onuldo_fe.data.challenge.dto.DailyCompletedResultDto
import com.example.onuldo_fe.data.challenge.dto.MyChallengeListResultDto
import com.example.onuldo_fe.data.challenge.dto.ParticipationRequestDto
import com.example.onuldo_fe.data.challenge.dto.ParticipationResultDto
import com.example.onuldo_fe.data.common.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

//챌린지 목록 조회
interface ChallengeApi {
    @GET("api/challenges")
    suspend fun getChallenges(
        @Query("size") size: Int,
        @Query("category") category: String?,  // ChallengeCategory API 값(FITNESS 등)
        @Query("s") search: String?            // 검색어
    ): ChallengeListResultDto

    // 챌린지 상세 조회 //TODO: ID 필요
    @GET("api/challenges/{challengeId}")
    suspend fun getChallengeDetail(
        @Path("challengeId") challengeId: Long
    ): ApiResponse<ChallengeItemDto>

    // 개인 유저의 챌린지 참여 - 도전금/기간 입력, 포인트 차감
    @POST("api/challenges/{challengeId}/participations")
    suspend fun participate(
        @Path("challengeId") challengeId: Long,
        @Body body: ParticipationRequestDto
    ): ApiResponse<ParticipationResultDto>

    // 내 챌린지 참여 목록
    @GET("api/users/me/challenges")
    suspend fun getMyChallenges(
        @Query("status") status: String?,
        @Query("cursor") cursor: String?,
        @Query("size") size: Int             // 기본 10
    ): MyChallengeListResultDto

    //오늘 날짜의 챌린지 조회
    @GET("api/users/me/challenges/daily")
    suspend fun getDailyChallenges(): ApiResponse<DailyChallengeResultDto>

    //오늘 완료한 챌린지 목록 조회
    @GET("api/users/me/challenges/daily/completed")
    suspend fun getDailyCompleted(): ApiResponse<DailyCompletedResultDto>
}

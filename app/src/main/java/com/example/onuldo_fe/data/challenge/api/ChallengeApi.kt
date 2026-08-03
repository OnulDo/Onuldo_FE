package com.example.onuldo_fe.data.challenge.api

import com.example.onuldo_fe.data.challenge.dto.ChallengeItemDto
import com.example.onuldo_fe.data.challenge.dto.ChallengeListResultDto
import com.example.onuldo_fe.data.challenge.dto.ParticipationRequestDto
import com.example.onuldo_fe.data.challenge.dto.ParticipationResultDto
import com.example.onuldo_fe.data.common.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// 챌린지 목록 조회 API
// 실제 Retrofit 인스턴스 생성은 팀 공통 네트워크 모듈(RetrofitClient) 담당이며,
// 응답은 공통 래퍼 ApiResponse<T>로
interface ChallengeApi {
    // 목록 응답은 최상위에 content/nextCursor/hasNext가 오는 커서 구조라 ApiResponse 래퍼를 쓰지 않는다. (클로드)
    @GET("api/challenges")
    suspend fun getChallenges(
        @Query("size") size: Int,
        @Query("category") category: String?,  // ChallengeCategory API 값(FITNESS 등)
        @Query("s") search: String?            // 검색어
    ): ChallengeListResultDto

    // 챌린지 상세 조회
    // 응답 result는 목록 아이템과 동일 스키마라 ChallengeItemDto를 재사용
    @GET("api/challenges/{challengeId}")
    suspend fun getChallengeDetail(
        @Path("challengeId") challengeId: Long
    ): ApiResponse<ChallengeItemDto>

    // 챌린지 참여 — 도전금/기간 입력, 포인트 차감
    @POST("api/challenges/{challengeId}/participations")
    suspend fun participate(
        @Path("challengeId") challengeId: Long,
        @Body body: ParticipationRequestDto
    ): ApiResponse<ParticipationResultDto>
}

package com.example.onuldo_fe.data.challenge.api

import com.example.onuldo_fe.data.challenge.dto.ChallengeItemDto
import com.example.onuldo_fe.data.challenge.dto.ChallengeListResultDto
import com.example.onuldo_fe.data.common.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// 챌린지 목록 조회 API
// 실제 Retrofit 인스턴스 생성은 팀 공통 네트워크 모듈(RetrofitClient) 담당이며,
// 응답은 공통 래퍼 ApiResponse<T>로
interface ChallengeApi {
    @GET("api/challenges")
    suspend fun getChallenges(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("category") category: String?,  // ChallengeCategory API 값(FITNESS 등)
        @Query("s") search: String?            // 검색어
    ): ApiResponse<ChallengeListResultDto>

    // 챌린지 상세 조회
    // 응답 result는 목록 아이템과 동일 스키마라 ChallengeItemDto를 재사용
    @GET("api/challenges/{challengeId}")
    suspend fun getChallengeDetail(
        @Path("challengeId") challengeId: Long
    ): ApiResponse<ChallengeItemDto>
}

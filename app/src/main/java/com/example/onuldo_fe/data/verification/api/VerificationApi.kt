package com.example.onuldo_fe.data.verification.api

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.verification.dto.ChallengeVerificationRequestDto
import com.example.onuldo_fe.data.verification.dto.ChallengeVerificationResultDto
import com.example.onuldo_fe.data.verification.dto.ImageUploadResultDto
import com.example.onuldo_fe.data.verification.dto.ManualReviewResultDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface VerificationApi {
    @Multipart
    @POST("api/file/images")
    suspend fun uploadImage(@Part file: MultipartBody.Part): ApiResponse<ImageUploadResultDto>

    @POST("api/challenges/{challengeId}/verification")
    suspend fun verifyChallenge(
        @Path("challengeId") challengeId: Long,
        @Body request: ChallengeVerificationRequestDto
    ): ApiResponse<ChallengeVerificationResultDto>

    @POST("api/challenges/{challengeId}/verification/manual-review")
    suspend fun requestManualReview(
        @Path("challengeId") challengeId: Long
    ): ApiResponse<ManualReviewResultDto>
}

package com.example.onuldo_fe.data.verification.api

import com.example.onuldo_fe.data.common.ApiResponse
import com.example.onuldo_fe.data.verification.dto.ImageUploadResultDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface VerificationApi {
    @Multipart
    @POST("api/file/images")
    suspend fun uploadImage(@Part file: MultipartBody.Part): ApiResponse<ImageUploadResultDto>
}
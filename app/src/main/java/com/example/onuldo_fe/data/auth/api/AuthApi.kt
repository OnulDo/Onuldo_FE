package com.example.onuldo_fe.data.auth.api

import com.example.onuldo_fe.data.auth.dto.EmailLoginRequest
import com.example.onuldo_fe.data.auth.dto.EmailSignupRequest
import com.example.onuldo_fe.data.auth.dto.OAuthLoginRequest
import com.example.onuldo_fe.data.auth.dto.OAuthLoginResponse
import com.example.onuldo_fe.data.auth.dto.OAuthSignupRequest
import com.example.onuldo_fe.data.network.AuthTokenResponse
import com.example.onuldo_fe.data.network.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/** 인증 API. 토큰 재발급은 네트워크 계층의 `TokenRefreshApi`가 담당한다. */
interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body request: EmailLoginRequest): Response<BaseResponse<AuthTokenResponse>>

    @POST("api/auth/signup")
    suspend fun signup(@Body request: EmailSignupRequest): Response<BaseResponse<AuthTokenResponse>>

    @POST("api/auth/oauth/login")
    suspend fun oauthLogin(
        @Body request: OAuthLoginRequest,
    ): Response<BaseResponse<OAuthLoginResponse>>

    @POST("api/auth/oauth/signup")
    suspend fun oauthSignup(
        @Body request: OAuthSignupRequest,
    ): Response<BaseResponse<AuthTokenResponse>>
}

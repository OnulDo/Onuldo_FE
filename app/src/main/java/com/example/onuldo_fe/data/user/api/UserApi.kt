package com.example.onuldo_fe.data.user.api

import com.example.onuldo_fe.data.network.BaseResponse
import com.example.onuldo_fe.data.network.CursorPageResponse
import com.example.onuldo_fe.data.user.dto.ChargePointRequestDto
import com.example.onuldo_fe.data.user.dto.ChargePointResponseDto
import com.example.onuldo_fe.data.user.dto.MyPageResponseDto
import com.example.onuldo_fe.data.user.dto.NotificationSettingsResponseDto
import com.example.onuldo_fe.data.user.dto.PointTransactionResponseDto
import com.example.onuldo_fe.data.user.dto.ProfileResponseDto
import com.example.onuldo_fe.data.user.dto.UpdateNotificationRequestDto
import com.example.onuldo_fe.data.user.dto.UpdateNotificationResponseDto
import com.example.onuldo_fe.data.user.dto.WalletSummaryResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

/** 사용자·포인트 지갑 API. 모두 인증 토큰이 필요하다. */
interface UserApi {

    @GET("api/users/me")
    suspend fun getMyPage(): Response<BaseResponse<MyPageResponseDto>>

    @GET("api/users/me/profile")
    suspend fun getProfile(): Response<BaseResponse<ProfileResponseDto>>

    @GET("api/users/me/notification-settings")
    suspend fun getNotificationSettings(): Response<BaseResponse<NotificationSettingsResponseDto>>

    @PATCH("api/users/me/notification-settings")
    suspend fun updateNotificationSetting(
        @Body request: UpdateNotificationRequestDto,
    ): Response<BaseResponse<UpdateNotificationResponseDto>>

    @GET("api/users/me/wallet/summary")
    suspend fun getWalletSummary(): Response<BaseResponse<WalletSummaryResponseDto>>

    /** [type]이 null이면 전체 내역. [cursor]는 다음 페이지 요청 시 이전 응답의 `nextCursor`. */
    @GET("api/users/me/wallet/transactions")
    suspend fun getWalletTransactions(
        @Query("type") type: String? = null,
        @Query("cursor") cursor: String? = null,
        @Query("size") size: Int = DEFAULT_PAGE_SIZE,
    ): Response<CursorPageResponse<PointTransactionResponseDto>>

    @POST("api/users/me/wallet/charges")
    suspend fun chargePoint(
        @Body request: ChargePointRequestDto,
    ): Response<BaseResponse<ChargePointResponseDto>>

    @POST("api/users/me/wallet/signup-bonuses")
    suspend fun grantSignupBonus(
        @Body request: ChargePointRequestDto,
    ): Response<BaseResponse<ChargePointResponseDto>>

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }
}

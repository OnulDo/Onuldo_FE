package com.example.onuldo_fe.data.user.api

import com.example.onuldo_fe.data.network.BaseResponse
import com.example.onuldo_fe.data.network.CursorPageResponse
import com.example.onuldo_fe.data.user.dto.ChargePointRequestDto
import com.example.onuldo_fe.data.user.dto.ChargePointResponseDto
import com.example.onuldo_fe.data.user.dto.MyPageResponseDto
import com.example.onuldo_fe.data.user.dto.PointTransactionResponseDto
import com.example.onuldo_fe.data.user.dto.ProfileResponseDto
import com.example.onuldo_fe.data.user.dto.UpdateProfileRequestDto
import com.example.onuldo_fe.data.user.dto.WalletSummaryResponseDto
import com.example.onuldo_fe.data.user.dto.WithdrawPointRequestDto
import com.example.onuldo_fe.data.user.dto.WithdrawPointResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

/** 사용자·포인트 지갑 API. 모두 인증 토큰이 필요하다. */
interface UserApi {

    @GET("api/users/me")
    suspend fun getMyPage(): Response<BaseResponse<MyPageResponseDto>>

    /** 현재 로그인한 사용자의 계정을 탈퇴 처리한다. 이후 기존 토큰으로 API 접근이 차단 */
    @DELETE("api/users/me")
    suspend fun deleteAccount(): Response<BaseResponse<Unit>>

    @GET("api/users/me/profile")
    suspend fun getProfile(): Response<BaseResponse<ProfileResponseDto>>

    /** 프로필 사진/닉네임 변경. 채운 필드만 변경되고 null은 기존 값이 유지된다. */
    @PATCH("api/users/me/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequestDto,
    ): Response<BaseResponse<ProfileResponseDto>>
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

    /** 포인트 출금. 출금된 금액과 출금 후 잔액을 돌려준다. */
    @POST("api/users/me/wallet/withdrawals")
    suspend fun withdrawPoint(
        @Body request: WithdrawPointRequestDto,
    ): Response<BaseResponse<WithdrawPointResponseDto>>

    @POST("api/users/me/wallet/signup-bonuses")
    suspend fun grantSignupBonus(
        @Body request: ChargePointRequestDto,
    ): Response<BaseResponse<ChargePointResponseDto>>

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }
}

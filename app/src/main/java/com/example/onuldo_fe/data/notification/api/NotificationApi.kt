package com.example.onuldo_fe.data.notification.api

import com.example.onuldo_fe.data.network.BaseResponse
import com.example.onuldo_fe.data.network.CursorPageResponse
import com.example.onuldo_fe.data.notification.dto.NotificationListItemDto
import com.example.onuldo_fe.data.notification.dto.NotificationSettingsResponseDto
import com.example.onuldo_fe.data.notification.dto.UpdateNotificationRequestDto
import com.example.onuldo_fe.data.notification.dto.UpdateNotificationResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

/** 알림함 API. 인증 토큰이 필수!! */
interface NotificationApi {

    @GET("api/users/me/notification-settings")
    suspend fun getNotificationSettings(): Response<BaseResponse<NotificationSettingsResponseDto>>

    @PATCH("api/users/me/notification-settings")
    suspend fun updateNotificationSetting(
        @Body request: UpdateNotificationRequestDto,
    ): Response<BaseResponse<UpdateNotificationResponseDto>>

    /**
     * 로그인 유저의 알림 목록을 최신순 커서 페이징으로 조회한다.
     * [cursor]는 이전 응답의 `nextCursor`(첫 페이지는 null)
     */
    @GET("api/users/me/notifications")
    suspend fun getNotifications(
        @Query("cursor") cursor: String? = null,
        @Query("size") size: Int = DEFAULT_PAGE_SIZE,
    ): Response<CursorPageResponse<NotificationListItemDto>>

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}

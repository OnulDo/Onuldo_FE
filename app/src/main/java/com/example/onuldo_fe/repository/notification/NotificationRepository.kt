package com.example.onuldo_fe.repository.notification

import com.example.onuldo_fe.data.network.ApiResult
import com.example.onuldo_fe.data.network.CursorPage
import com.example.onuldo_fe.model.home.notification.NotificationItem

interface NotificationRepository {
    /** 알림 목록을 최신순 커서 페이징으로 조회한다. [cursor]는 이전 응답의 nextCursor(첫 페이지는 null). */
    suspend fun getNotifications(cursor: String? = null): ApiResult<CursorPage<NotificationItem>>
}

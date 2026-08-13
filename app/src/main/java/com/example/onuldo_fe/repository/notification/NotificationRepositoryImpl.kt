package com.example.onuldo_fe.repository.notification

import com.example.onuldo_fe.data.network.ApiResult
import com.example.onuldo_fe.data.network.CursorPage
import com.example.onuldo_fe.data.network.map
import com.example.onuldo_fe.data.network.safeCursorApiCall
import com.example.onuldo_fe.data.notification.api.NotificationApi
import com.example.onuldo_fe.data.notification.dto.NotificationListItemDto
import com.example.onuldo_fe.model.home.notification.NotificationItem
import com.example.onuldo_fe.model.home.notification.notificationTypeFrom

class NotificationRepositoryImpl(
    private val api: NotificationApi,
) : NotificationRepository {

    override suspend fun getNotifications(cursor: String?): ApiResult<CursorPage<NotificationItem>> =
        safeCursorApiCall { api.getNotifications(cursor = cursor) }.map { page ->
            CursorPage(
                items = page.items.map { it.toModel() },
                nextCursor = page.nextCursor,
                hasNext = page.hasNext,
            )
        }
}

private fun NotificationListItemDto.toModel() = NotificationItem(
    notificationId = notificationId,
    title = title,
    content = content,
    timeAgo = timeAgo,
    createdAt = createdAt,
    type = notificationTypeFrom(type, title),
    challengeId = challengeId,
    partyId = partyId,
)

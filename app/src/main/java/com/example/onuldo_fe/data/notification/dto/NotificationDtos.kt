package com.example.onuldo_fe.data.notification.dto

/**
 * `GET /api/users/me/notifications` 목록 항목.
 *
 * 봉투는 공통 커서 페이지(`content`/`nextCursor`/`hasNext`)를 쓴다.
 * [challengeId]·[partyId]는 이동 대상에 따라 한쪽만 내려올 수 있어 nullable로 둔다.
 * 보관 기간은 30일이며, 그 이후 항목은 조회되지 않는다.
 */
data class NotificationListItemDto(
    val notificationId: Long = 0,
    val type: String = "",              // 서버 알림 종류 enum 문자열
    val challengeId: Long? = null,      // 챌린지로 이동할 때 사용
    val partyId: Long? = null,          // 파티로 이동할 때 사용
    val title: String = "",
    val content: String = "",
    val timeAgo: String = "",
    val createdAt: String = "",         // ISO-8601 date-time
)

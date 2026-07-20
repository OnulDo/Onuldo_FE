package com.example.onuldo_fe.ui.screen.home.notification

data class NotificationItem(
    val title: String,
    val description: String,
    val timeAgo: String,
    val type: NotificationType
)

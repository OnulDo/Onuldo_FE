package com.example.onuldo_fe.ui.screen.home.notification

object NotificationDummyData {
    val items = listOf(
        NotificationItem(
            title = "인증 마감 30분 전이에요",
            description = "30분 러닝 챌린지 인증을 잊지 마세요",
            timeAgo = "방금",
            type = NotificationType.Deadline
        ),
        NotificationItem(
            title = "인증이 완료되었어요",
            description = "새벽 기상 챌린지 인증 성공 (+850P)",
            timeAgo = "5시간 전",
            type = NotificationType.Success
        ),
        NotificationItem(
            title = "새 챌린지가 시작되었어요",
            description = "오늘부터 30분 러닝 챌린지가 시작됐어요",
            timeAgo = "어제",
            type = NotificationType.NewChallenge
        ),
        NotificationItem(
            title = "환급이 완료되었어요",
            description = "독서 30분 챌린지 환급 18,400P 지급",
            timeAgo = "2일 전",
            type = NotificationType.Settlement
        ),
        NotificationItem(
            title = "인증 실패로 차감되었어요",
            description = "5/17 새벽 기상 인증 미수행 (−850P)",
            timeAgo = "3일 전",
            type = NotificationType.Failure
        )
    )
}

package com.example.onuldo_fe.repository.notification

import com.example.onuldo_fe.model.home.notification.NotificationData
import com.example.onuldo_fe.model.home.notification.NotificationItem
import com.example.onuldo_fe.model.home.notification.NotificationType
import java.time.LocalDateTime

class NotificationRepositoryImpl : NotificationRepository {
    override fun getNotifications(): NotificationData {
        // API 연동 전 더미 데이터 — 명세서 확정되면 DTO/API 레이어 추가 후 교체
        return NotificationData(notifications = dummyNotifications)
    }
}

// 표시 유형 10종 더미 — 실제 시각(createdAt)으로 보유, 표기는 RelativeTime이 변환. 최신순
// 포인트/닉네임/사유 등은 상세 문구에 포함 (실제 값은 정산 도메인·NOTI 정책 따름)
private val now: LocalDateTime = LocalDateTime.now()

private val dummyNotifications = listOf(
    NotificationItem(
        title = "지금 인증할 시간이에요",
        content = "새벽 6시 기상 인증이 시작됐어요. 06:30까지 완료해주세요!",
        createdAt = now,
        type = NotificationType.DeadlineReminder
    ),
    NotificationItem(
        title = "오늘 인증, 잊지 않으셨죠?",
        content = "독서 30분 인증은 23시까지! 지금 완료해보세요",
        createdAt = now.minusHours(1),
        type = NotificationType.DeadlineReminder
    ),
    NotificationItem(
        title = "마감 1시간 전이에요",
        content = "23시까지 인증하지 않으면 오늘 독서 30분은 실패 처리돼요",
        createdAt = now.minusHours(2),
        type = NotificationType.DeadlineWarning
    ),
    NotificationItem(
        title = "인증이 승인됐어요",
        content = "30분 러닝 인증이 검토를 통과했어요. 오늘도 성공!",
        createdAt = now.minusHours(3),
        type = NotificationType.ReviewPassed
    ),
    NotificationItem(
        title = "인증이 승인되지 않았어요",
        content = "30분 러닝 인증 실패 처리\n사유: 러닝 기록 미확인",
        createdAt = now.minusHours(4),
        type = NotificationType.ReviewRejected
    ),
    NotificationItem(
        title = "동동님이 인증을 완료했어요",
        content = "새벽 6시 기상 파티 피드에서 확인해보세요",
        createdAt = now.minusHours(5),
        type = NotificationType.PartyMemberVerified
    ),
    NotificationItem(
        title = "30분 러닝, 오늘부터 시작!",
        content = "1일차예요. 첫 인증으로 기분 좋게 출발해요",
        createdAt = now.minusDays(1),
        type = NotificationType.ChallengeStart
    ),
    NotificationItem(
        title = "독서 30분 종료까지 3일",
        content = "완주가 눈앞이에요. 마지막까지 함께해요!",
        createdAt = now.minusDays(1),
        type = NotificationType.ChallengeEndReminder
    ),
    NotificationItem(
        title = "포인트가 지급됐어요",
        content = "독서 30분 정산 완료! 21,000P가 들어왔어요",
        createdAt = now.minusDays(2),
        type = NotificationType.SoloRefund
    ),
    NotificationItem(
        title = "파티 정산이 완료됐어요",
        content = "새벽 6시 기상 정산 결과 23,100P가 들어왔어요",
        createdAt = now.minusDays(2),
        type = NotificationType.PartySettlement
    ),
    NotificationItem(
        title = "오늘의 파티 몫 +666P",
        content = "새벽 6시 기상 오늘 몫이 확정됐어요 · 종료 시 지급",
        createdAt = now.minusDays(1),
        type = NotificationType.PartyDailySettlement
    )
).sortedByDescending { it.createdAt } // 최신순 보장 (화면은 받은 순서대로 표시)

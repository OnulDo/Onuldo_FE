package com.example.onuldo_fe.repository.notification

import com.example.onuldo_fe.model.home.notification.NotificationData
import com.example.onuldo_fe.model.home.notification.NotificationItem
import com.example.onuldo_fe.model.home.notification.NotificationType

class NotificationRepositoryImpl : NotificationRepository {
    override fun getNotifications(): NotificationData {
        // API 연동 전 더미 데이터 — 명세서 확정되면 DTO/API 레이어 추가 후 교체
        return NotificationData(notifications = dummyNotifications)
    }
}

private val dummyNotifications = listOf(
    NotificationItem("인증 마감 30분 전이에요", "30분 러닝 챌린지 인증을 잊지 마세요", "방금", NotificationType.Deadline),
    NotificationItem("인증이 완료되었어요", "새벽 기상 챌린지 인증 성공 (+850P)", "5시간 전", NotificationType.VerificationSuccess),
    NotificationItem("새 챌린지가 시작되었어요", "오늘부터 러닝 챌린지가 시작됐어요", "어제", NotificationType.ChallengeStart),
    NotificationItem("환급이 완료되었어요", "독서 30분 챌린지 환급 18,400P 지급", "2월 3일", NotificationType.Refund),
    NotificationItem("인증 실패로 차감되었어요", "5/17 새벽 기상 인증 미수행 (-850P)", "3일 전", NotificationType.VerificationFail)
)

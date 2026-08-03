package com.example.onuldo_fe.model.home.notification

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

// 알림 상대 시각 표기 — API가 어떤 시각 값을 주든 LocalDateTime으로만 받으면
// 여기서 "방금 / N시간 전 / 어제 / N일 전"으로 변환해서 내보낸다.
sealed interface RelativeTime {
    val label: String

    data object Now : RelativeTime {
        override val label get() = "방금"
    }

    data class HoursAgo(val hours: Int) : RelativeTime {
        override val label get() = "${hours}시간 전"
    }

    data object Yesterday : RelativeTime {
        override val label get() = "어제"
    }

    data class DaysAgo(val days: Int) : RelativeTime {
        override val label get() = "${days}일 전"
    }

    companion object {
        // TODO(PM 확인): 표기 형식만 정책 정의됨. 경계값(방금 기준·24h vs 날짜·최대 N일·절대날짜 전환)은 미정 → 아래는 임의 기본값
        // 방금: 1시간 미만 / N시간 전: 오늘(달력 같은 날) / 어제: 전날 / N일 전: 그 이전(달력일 기준)
        fun from(createdAt: LocalDateTime, now: LocalDateTime = LocalDateTime.now()): RelativeTime {
            val minutes = ChronoUnit.MINUTES.between(createdAt, now).coerceAtLeast(0)
            val calendarDays = ChronoUnit.DAYS.between(createdAt.toLocalDate(), now.toLocalDate())
            return when {
                minutes < 60 -> Now
                calendarDays == 0L -> HoursAgo((minutes / 60).toInt())
                calendarDays == 1L -> Yesterday
                else -> DaysAgo(calendarDays.toInt())
            }
        }
    }
}

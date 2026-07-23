package com.example.onuldo_fe.model.home

// 홈 상단에 표시할 오늘의 전체 챌린지 진행 현황
data class TodayChallenge(
    val date: String,                          // 화면에 표시할 오늘 날짜
    val progress: Float,                       // 오늘 챌린지 전체 진행률
    val completedCount: Int = 0,              // 오늘 완료한 챌린지 수
    val totalCount: Int = 0                    // 오늘 진행할 전체 챌린지 수
)

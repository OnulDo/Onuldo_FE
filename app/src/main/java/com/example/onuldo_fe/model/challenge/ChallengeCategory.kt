package com.example.onuldo_fe.model.challenge

// 챌린지 목록 API의 category Enum과 화면에 표시할 한글 이름을 함께 관리
enum class ChallengeCategory(val displayName: String) {
    FITNESS("피트니스"),
    HOBBY("취미"),
    SELF_IMPROVEMENT("자기계발"),
    DAILY_ROUTINE("생활루틴"),
    DIETARY_HABIT("식습관")
}

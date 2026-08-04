package com.example.onuldo_fe.model.challenge

// 챌린지 목록 API의 category Enum과 화면에 표시할 한글 이름을 함께 관리
// enum 이름은 서버 API category과 동일하게 수정
enum class ChallengeCategory(val displayName: String) {
    FITNESS("피트니스"),
    HOBBY("취미"),
    PERSONAL_DEVELOPMENT("자기계발"),
    LIFESTYLE_ROUTINE("생활루틴"),
    EATING_HABITS("식습관")
}

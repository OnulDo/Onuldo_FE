package com.example.onuldo_fe.model.home

// 홈의 오늘 완료 챌린지 영역에 표시할 챌린지 유형별 정보
sealed interface HomeCompletedChallenge {
    val time: String                          // 인증 완료 시간 문구
    val title: String                         // 챌린지 이름

    // 함께하는 파티 챌린지의 완료 정보
    data class Party(
        override val time: String,             // 파티 챌린지 인증 완료 시간 문구
        override val title: String,            // 파티 챌린지 이름
        val completedMemberCount: Int,         // 오늘 인증을 완료한 파티원 수
        val totalMemberCount: Int              // 현재 참여 중인 전체 파티원 수
    ) : HomeCompletedChallenge

    // 개인 챌린지의 완료 정보
    data class Personal(
        override val time: String,             // 개인 챌린지 인증 완료 시간 문구
        override val title: String,            // 개인 챌린지 이름
        val streakDays: Int                    // 현재 연속 성공 일수
    ) : HomeCompletedChallenge
}

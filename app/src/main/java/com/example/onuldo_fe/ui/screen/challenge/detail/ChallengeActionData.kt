package com.example.onuldo_fe.ui.screen.challenge.detail

/**
 * 상세 화면 CTA 클릭 시 호출부로 전달하는 챌린지 정보.
 * 참여/파티 생성 등 후속 흐름에서 재사용한다.
 */
data class ChallengeActionData(
    val challengeId: Long,
    val title: String,
    val description: String,
    val category: String,
    val timeStart: String,
    val timeEnd: String
)

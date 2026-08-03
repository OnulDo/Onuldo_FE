package com.example.onuldo_fe.viewmodel.challenge

import com.example.onuldo_fe.model.challenge.ParticipationResult

// 챌린지 참여 화면 상태 — 제출중/성공(result)/에러
data class ParticipateUiState(
    val isSubmitting: Boolean = false,
    val result: ParticipationResult? = null,
    val isError: Boolean = false,            // 일반 실패 → 토스트
    val isInsufficientPoint: Boolean = false // 포인트 부족 → 충전 팝업
)

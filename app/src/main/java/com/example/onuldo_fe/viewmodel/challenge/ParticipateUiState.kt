package com.example.onuldo_fe.viewmodel.challenge

import com.example.onuldo_fe.model.challenge.ParticipationResult

// 챌린지 참여 화면 상태 — 제출중/성공(result)/에러 + 보유 포인트(지갑 잔액)
data class ParticipateUiState(
    val isSubmitting: Boolean = false,
    val result: ParticipationResult? = null,
    val isError: Boolean = false,            // 일반 실패 → 토스트
    val isInsufficientPoint: Boolean = false, // 포인트 부족 → 충전 팝업
    val isAlreadyParticipating: Boolean = false, // 이미 참여중 → "이미 참여중입니다" 토스트 + 복귀
    val balance: Int = 0                      // 지갑 요약의 보유 포인트(balance)
)

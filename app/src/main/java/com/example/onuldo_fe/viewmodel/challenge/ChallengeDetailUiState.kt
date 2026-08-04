package com.example.onuldo_fe.viewmodel.challenge

import com.example.onuldo_fe.model.challenge.ChallengeDetail

// 챌린지 상세 화면 상태 — 로딩/에러/성공(detail)을 하나의 상태로 표현
data class ChallengeDetailUiState(
    val detail: ChallengeDetail? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

package com.example.onuldo_fe.viewmodel.challenge

import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

// 챌린지 목록 화면 상태 (최소 범위 — 무한스크롤/상태 UI는 후속 PR)
data class ChallengeListUiState(
    val challenges: List<Challenge> = emptyList(),
    val selectedCategory: ChallengeCategory? = null,  // null이면 전체
    val query: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

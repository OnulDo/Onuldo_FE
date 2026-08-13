package com.example.onuldo_fe.viewmodel.challenge

import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

// 챌린지 목록 화면 상태 (최소 범위 — 무한스크롤/상태 UI는 후속 PR)
data class ChallengeListUiState(
    val challenges: List<Challenge> = emptyList(),
    val selectedCategory: ChallengeCategory? = null,  // null이면 전체
    val query: String = "",
    // 최초/필터 변경 시 빈 목록 위에 표시하는 전체 화면 로딩
    val isLoading: Boolean = false,
    // 기존 목록을 유지한 채 재조회할 때 표시하는 상단 새로고침 인디케이터
    val isRefreshing: Boolean = false,
    val isError: Boolean = false,
    // 실패 시 화면에 띄울 문구 — 서버 메시지 그대로 / 네트워크 끊김이면 클라 기본 문구 (isError와 함께 세팅)
    val errorMessage: String? = null,
    // 최초 조회가 한 번이라도 성공했는지 — 화면 복귀 시 새로고침 트리거 조건
    val hasLoaded: Boolean = false
)

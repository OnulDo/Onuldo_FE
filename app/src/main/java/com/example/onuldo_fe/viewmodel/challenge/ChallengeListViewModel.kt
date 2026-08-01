package com.example.onuldo_fe.viewmodel.challenge

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.repository.challenge.ChallengeRepository
import com.example.onuldo_fe.repository.challenge.ChallengeRepositoryProvider
import kotlinx.coroutines.launch

class ChallengeListViewModel(
    private val repository: ChallengeRepository = ChallengeRepositoryProvider.provide()
) : ViewModel() {

    var uiState by mutableStateOf(ChallengeListUiState())
        private set

    // 페이지네이션(무한 스크롤)은 후속 PR — 우선 첫 페이지만 조회
    private val pageSize = 10

    init { load() }

    // 카테고리 선택/해제 → 다시 조회
    fun onCategorySelected(category: ChallengeCategory?) {
        if (uiState.selectedCategory == category) return
        uiState = uiState.copy(selectedCategory = category)
        load()
    }

    // 검색어 반영 후 조회 (최소 구현 — 디바운스 등 최적화는 후속 PR)
    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query)
        if (query.isBlank()) {
            // 검색창을 비우면 전체가 아니라 빈 목록을 보여준다 ("검색 결과 없음" 상태).
            // 첫 진입/카테고리 조회(load)는 이 경로를 안 타므로 전체가 그대로 유지됨.
            uiState = uiState.copy(challenges = emptyList(), isLoading = false, isError = false)
        } else {
            load()
        }
    }

    private fun load() {
        uiState = uiState.copy(isLoading = true, isError = false)
        viewModelScope.launch {
            runCatching {
                repository.getChallenges(
                    page = 0,
                    size = pageSize,
                    category = uiState.selectedCategory,
                    search = uiState.query
                )
            }.onSuccess { page ->
                uiState = uiState.copy(challenges = page.challenges, isLoading = false)
            }.onFailure {
                uiState = uiState.copy(isLoading = false, isError = true)
            }
        }
    }
}

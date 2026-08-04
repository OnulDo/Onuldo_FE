package com.example.onuldo_fe.viewmodel.challenge

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.repository.challenge.ChallengeRepository
import com.example.onuldo_fe.repository.challenge.ChallengeRepositoryProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChallengeListViewModel(
    private val repository: ChallengeRepository = ChallengeRepositoryProvider.provide()
) : ViewModel() {

    var uiState by mutableStateOf(ChallengeListUiState())
        private set

    // 페이지네이션(무한 스크롤)은 후속 PR — 우선 첫 페이지만 조회
    private val pageSize = 10

    // 진행 중인 조회 코루틴. 새 조회가 시작되면 이전 것을 취소(디바운스 + 최신 요청만 유지).
    private var loadJob: Job? = null

    // 요청 세대 — 취소를 못 받고 지연 도착한 이전 응답이 최신을 덮어쓰는 것 방지(백스톱)
    private var requestGeneration = 0

    init { scheduleLoad(debounceMs = 0) }

    // 카테고리 선택/해제 → 즉시 재조회 (검색어와 AND 조합)
    fun onCategorySelected(category: ChallengeCategory?) {
        if (uiState.selectedCategory == category) return
        uiState = uiState.copy(selectedCategory = category)
        scheduleLoad(debounceMs = 0)
    }

    // 검색어 입력 → 0.3초 디바운스 후 재조회.
    // 공백만 입력/비움이면 검색어 없이 조회 → 전체(선택된 카테고리 있으면 그 필터)만 표시.
    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query)
        scheduleLoad(debounceMs = SEARCH_DEBOUNCE_MS)
    }

    // 에러 토스트 1회 노출 후 소비 (화면은 빈 상태로 유지)
    fun onErrorShown() { uiState = uiState.copy(isError = false) }

    // 이전 조회를 취소하고 (필요 시 디바운스 후) 새 조회 시작
    private fun scheduleLoad(debounceMs: Long) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (debounceMs > 0) delay(debounceMs)
            load()
        }
    }

    private suspend fun load() {
        // 이번 요청의 세대와 조회 조건(카테고리/검색어)을 시작 시점에 고정(capture)
        val generation = ++requestGeneration
        val category = uiState.selectedCategory
        val query = uiState.query   // 공백/빈 문자열이면 Repository에서 keyword 미전송 → 전체 조회
        uiState = uiState.copy(isLoading = true, isError = false)
        runCatching {
            repository.getChallenges(
                page = 0,
                size = pageSize,
                category = category,
                search = query
            )
        }.onSuccess { page ->
            if (generation != requestGeneration) return@onSuccess
            uiState = uiState.copy(challenges = page.challenges, isLoading = false)
        }.onFailure { e ->
            if (e is CancellationException) throw e   // 취소는 실패로 처리하지 않음
            if (generation != requestGeneration) return@onFailure
            logChallengeError("ch_ls", e)
            uiState = uiState.copy(isLoading = false, isError = true)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
    }
}

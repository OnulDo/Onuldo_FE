package com.example.onuldo_fe.viewmodel.challenge

import android.util.Log
import com.example.onuldo_fe.BuildConfig
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.repository.challenge.ChallengeRepository
import com.example.onuldo_fe.repository.challenge.ChallengeRepositoryProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChallengeListViewModel(
    private val repository: ChallengeRepository = ChallengeRepositoryProvider.provide()
) : ViewModel() {

    var uiState by mutableStateOf(ChallengeListUiState())
        private set

    private val pageSize = 10

    private var loadJob: Job? = null

    // 요청 세대 — 취소를 못 받고 지연 도착한 이전 응답이 최신을 덮어쓰는 것 방지(백스톱)
    private var requestGeneration = 0

    init {
        scheduleLoad(debounceMs = 0)
    }

    fun onCategorySelected(category: ChallengeCategory?) {
        if (uiState.selectedCategory == category) return
        uiState = uiState.copy(selectedCategory = category)
        scheduleLoad(debounceMs = 0)
    }

    // 검색어 입력 → 0.3초 디바운스 후 재조회.
    // 공백만 입력/비움이면 검색어 없이 조회 → 전체(선택된 카테고리 있으면 그 필터)만 표시
    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query)
        scheduleLoad(debounceMs = SEARCH_DEBOUNCE_MS)
    }

    fun onErrorShown() {
        uiState = uiState.copy(isError = false, errorMessage = null)
    }

    // 조회 표시 방식 — FULL: 전체 화면 로딩, REFRESH: 상단 인디케이터, SILENT: 표시 없이 데이터만 갱신
    private enum class LoadMode { FULL, REFRESH, SILENT }

    // 당겨서 새로고침(pull-to-refresh) — 상단 인디케이터를 표시하며 재조회
    // 예외) 이미 조회 중이면 무시해 중복 호출을 막는다
    fun refresh() {
        if (uiState.isLoading || uiState.isRefreshing) return
        scheduleLoad(debounceMs = 0, mode = LoadMode.REFRESH)
    }

    // 화면 복귀(ON_RESUME)용 조용한 재조회 — 로딩/새로고침 표시 없이 기존 목록을 최신으로 교체만 한다.
    // 예외) 이미 조회 중이면 무시한다
    fun silentRefresh() {
        if (uiState.isLoading || uiState.isRefreshing) return
        scheduleLoad(debounceMs = 0, mode = LoadMode.SILENT)
    }

    // 이전 조회를 취소하고 (필요 시 디바운스 후) 새 조회 시작
    private fun scheduleLoad(debounceMs: Long, mode: LoadMode = LoadMode.FULL) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (debounceMs > 0) delay(debounceMs)
            load(mode)
        }
    }

    private suspend fun load(mode: LoadMode = LoadMode.FULL) {
        val generation = ++requestGeneration
        val category = uiState.selectedCategory
        val query = uiState.query

        val refreshStartTime = if (mode == LoadMode.REFRESH) System.currentTimeMillis() else 0L
        uiState = when (mode) {
            LoadMode.FULL -> uiState.copy(isLoading = true, isRefreshing = false, isError = false, errorMessage = null)
            LoadMode.REFRESH -> uiState.copy(isRefreshing = true, isError = false, errorMessage = null)
            LoadMode.SILENT -> uiState.copy(isError = false, errorMessage = null)
        }

        val result = repository.getChallenges(
            page = 0,
            size = pageSize,
            category = category,
            search = query
        )

        // 취소를 못 받고 지연 도착한 이전 세대 응답이 최신을 덮지 않도록 방어
        if (generation != requestGeneration) return

        result.onSuccess { page ->
            // 챌린지 전체 목록은 toString에 imageUrl 등 메타가 다 찍히므로 디버그 빌드에서만 남긴다
            if (BuildConfig.DEBUG) {
                Log.d(
                    "ChallengeDebug",
                    "generation=$generation, mode=$mode, count=${page.challenges.size}, challenges=${page.challenges}"
                )
            }
            // 당겨서 새로고침일 때만, 응답이 너무 빨라 인디케이터가 안 보이지 않도록 최소 300ms 유지
            if (mode == LoadMode.REFRESH) {
                val elapsed = System.currentTimeMillis() - refreshStartTime
                if (elapsed < REFRESH_INDICATOR_MIN_MS) {
                    delay(REFRESH_INDICATOR_MIN_MS - elapsed)
                }
            }

            uiState = uiState.copy(
                challenges = page.challenges,
                isLoading = false,
                isRefreshing = false,
                hasLoaded = true
            )

        }.onError { code, message ->

            logChallengeError("ch_ls", code, message)   // 서버 code/message - 개발자 확인용

    // FULL은 최초 조회나 조건 변경 후 조회이므로 실패 시 에러를 표시한다.
    // 이전 목록이 남아 있어도 새 조건의 결과가 아니므로 조용히 유지하면 혼동을 줄 수 있다.
    // REFRESH는 목록이 비어 있을 때만 표시하고, 목록이 있으면 기존 목록을 유지한다.
    // SILENT는 백그라운드 갱신이므로 실패를 표시하지 않는다.
            val shouldShowError = mode == LoadMode.FULL ||
                (mode == LoadMode.REFRESH && uiState.challenges.isEmpty())
            uiState = uiState.copy(
                isLoading = false,
                isRefreshing = false,
                isError = shouldShowError,
                errorMessage = if (shouldShowError) result.toChallengeErrorMessage() else null
            )
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L             //검색 API 호출 지연(입력없을 때)
        private const val REFRESH_INDICATOR_MIN_MS = 300L        // 새로고침 인디케이터 최소 노출 시간
    }
}
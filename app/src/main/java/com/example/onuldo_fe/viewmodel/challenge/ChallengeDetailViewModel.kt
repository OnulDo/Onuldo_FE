package com.example.onuldo_fe.viewmodel.challenge

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.onuldo_fe.repository.challenge.ChallengeRepository
import com.example.onuldo_fe.repository.challenge.ChallengeRepositoryProvider
import kotlinx.coroutines.launch

// 챌린지 상세 조회 ViewModel. challengeId는 화면 진입 시 확정되므로 생성 시점에 주입한다.
class ChallengeDetailViewModel(
    private val challengeId: Long,
    private val repository: ChallengeRepository = ChallengeRepositoryProvider.provide()
) : ViewModel() {

    var uiState by mutableStateOf(ChallengeDetailUiState())
        private set

    init { load() }

    private fun load() {
        uiState = uiState.copy(isLoading = true, isError = false)
        viewModelScope.launch {
            runCatching { repository.getChallengeDetail(challengeId) }
                .onSuccess { detail ->
                    uiState = uiState.copy(detail = detail, isLoading = false)
                }
                .onFailure { e ->
                    logChallengeError("ch_dt", e)
                    uiState = uiState.copy(isLoading = false, isError = true)
                }
        }
    }

    companion object {
        // challengeId를 생성자에 넘기기 위한 팩토리 (viewModel(factory = ...)에서 사용)
        fun factory(challengeId: Long) = viewModelFactory {
            initializer { ChallengeDetailViewModel(challengeId) }
        }
    }
}

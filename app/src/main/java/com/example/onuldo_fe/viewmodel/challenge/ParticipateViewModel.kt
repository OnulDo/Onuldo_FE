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

// 챌린지 참여(POST participations) ViewModel. challengeId는 진입 시 확정되므로 생성 시 주입.
class ParticipateViewModel(
    private val challengeId: Long,
    private val repository: ChallengeRepository = ChallengeRepositoryProvider.provide()
) : ViewModel() {

    var uiState by mutableStateOf(ParticipateUiState())
        private set

    fun participate(durationWeeks: Int, depositAmount: Int) {
        if (uiState.isSubmitting) return
        uiState = uiState.copy(isSubmitting = true, isError = false, isInsufficientPoint = false)
        viewModelScope.launch {
            runCatching { repository.participate(challengeId, depositAmount, durationWeeks) }
                .onSuccess { uiState = uiState.copy(isSubmitting = false, result = it) }
                .onFailure { e ->
                    val code = logChallengeError("ch_pd", e)
                    // 포인트 부족은 충전 팝업, 그 외는 일반 실패(토스트)
                    if (code == CODE_INSUFFICIENT_POINT) {
                        uiState = uiState.copy(isSubmitting = false, isInsufficientPoint = true)
                    } else {
                        uiState = uiState.copy(isSubmitting = false, isError = true)
                    }
                }
        }
    }

    // 각 상태 1회 노출 후 소비
    fun onErrorShown() { uiState = uiState.copy(isError = false) }
    fun onInsufficientDismissed() { uiState = uiState.copy(isInsufficientPoint = false) }

    companion object {
        private const val CODE_INSUFFICIENT_POINT = "INSUFFICIENT_POINT_FOR_CHALLENGE"

        fun factory(challengeId: Long) = viewModelFactory {
            initializer { ParticipateViewModel(challengeId) }
        }
    }
}

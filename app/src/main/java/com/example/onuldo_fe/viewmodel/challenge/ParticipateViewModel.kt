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

    // balance는 더미. 화면(포인트 부족 팝업의 "보유 포인트")까지 배선은 끝나 있음.
    var uiState by mutableStateOf(ParticipateUiState(balance = DUMMY_BALANCE))
        private set

    init {
        loadWallet()
    }

    // 보유 포인트(지갑 잔액) 로드 — 포인트 부족 안내(보유/필요/부족분)에 사용.
    // TODO(지갑 API 연동): 팀원의 GET /api/users/me/wallet/summary 가 올라오면
    //   아래 더미 대입을 그 응답의 result.balance 로 교체하면 바로 연동된다.
    //   예) viewModelScope.launch {
    //         runCatching { walletRepository.getWalletSummary() }
    //             .onSuccess { uiState = uiState.copy(balance = it.balance) }
    //             .onFailure { logChallengeError("ch_wallet", it) }
    //       }
    private fun loadWallet() {
        uiState = uiState.copy(balance = DUMMY_BALANCE)
    }

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
        // 지갑 API 연동 전 임시 보유 포인트. 팀원 API 올라오면 loadWallet()에서 실제 balance로 교체
        private const val DUMMY_BALANCE = 20_000

        fun factory(challengeId: Long) = viewModelFactory {
            initializer { ParticipateViewModel(challengeId) }
        }
    }
}

package com.example.onuldo_fe.viewmodel.challenge

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.repository.challenge.ChallengeRepository
import com.example.onuldo_fe.repository.challenge.ChallengeRepositoryProvider
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import kotlinx.coroutines.launch

// 챌린지 참여(POST participations) ViewModel. challengeId는 진입 시 확정되므로 생성 시 주입.
class ParticipateViewModel(
    private val challengeId: Long,
    private val repository: ChallengeRepository = ChallengeRepositoryProvider.provide(),
    private val userRepository: UserRepository = UserRepositoryProvider.provide()
) : ViewModel() {

    // 보유 포인트(balance)는 지갑 요약 API로 채운다. 화면(포인트 부족 팝업의 "보유 포인트")까지 배선 완료.
    var uiState by mutableStateOf(ParticipateUiState(balance = 0))
        private set

    init {
        loadWallet()
    }

    // 보유 포인트(지갑 잔액) 로드 — 포인트 부족 안내(보유/필요/부족분)에 사용.
    // GET /api/users/me/wallet/summary 의 balance를 사용. 실패 시 더미 대신 0을 유지해 오인을 막는다.
    private fun loadWallet() {
        viewModelScope.launch {
            userRepository.getWalletSummary()
                .onSuccess { summary -> uiState = uiState.copy(balance = summary.balance.toInt()) }
        }
    }

    fun participate(durationWeeks: Int, depositAmount: Int) {
        if (uiState.isSubmitting) return
        uiState = uiState.copy(
            isSubmitting = true, isError = false,
            isInsufficientPoint = false, isAlreadyParticipating = false
        )
        viewModelScope.launch {
            runCatching { repository.participate(challengeId, depositAmount, durationWeeks) }
                .onSuccess { uiState = uiState.copy(isSubmitting = false, result = it) }
                .onFailure { e ->
                    val code = logChallengeError("ch_pd", e)
                    // 이미 참여중은 오류가 아니라 안내(토스트+복귀), 포인트 부족은 충전 팝업, 그 외는 일반 실패(토스트)
                    uiState = when (code) {
                        CODE_ALREADY_PARTICIPATING ->
                            uiState.copy(isSubmitting = false, isAlreadyParticipating = true)
                        CODE_INSUFFICIENT_POINT ->
                            uiState.copy(isSubmitting = false, isInsufficientPoint = true)
                        else ->
                            uiState.copy(isSubmitting = false, isError = true)
                    }
                }
        }
    }

    // 각 상태 1회 노출 후 소비
    fun onErrorShown() { uiState = uiState.copy(isError = false) }
    fun onInsufficientDismissed() { uiState = uiState.copy(isInsufficientPoint = false) }
    fun onAlreadyParticipatingShown() { uiState = uiState.copy(isAlreadyParticipating = false) }

    companion object {
        private const val CODE_INSUFFICIENT_POINT = "INSUFFICIENT_POINT_FOR_CHALLENGE"
        private const val CODE_ALREADY_PARTICIPATING = "ALREADY_PARTICIPATING_CHALLENGE"

        fun factory(challengeId: Long) = viewModelFactory {
            initializer { ParticipateViewModel(challengeId) }
        }
    }
}

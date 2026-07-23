package com.example.onuldo_fe.viewmodel.party

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.onuldo_fe.R
import com.example.onuldo_fe.model.party.PartyChallenge
import com.example.onuldo_fe.repository.party.PartyChallengeRepository
import com.example.onuldo_fe.repository.party.PartyChallengeRepositoryProvider
import com.example.onuldo_fe.ui.screen.party.PartyChallengeCardUi
import com.example.onuldo_fe.ui.screen.party.PartyChallengeUi

// 파티에 연계할 챌린지 목록의 조회 상태와 UI 데이터 관리
class PartyChallengeSelectViewModel(
    private val repository: PartyChallengeRepository = PartyChallengeRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(PartyChallengeSelectUiState())
        private set

    init {
        // 챌린지 선택 화면 최초 진입 시 fake 또는 실제 API 목록 조회
        loadPartyChallenges()
    }

    fun loadPartyChallenges() {
        // 재시도 시 이전 오류를 지우고 로딩 상태로 전환
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.getPartyChallenges() }
                .onSuccess { challenges ->
                    uiState = PartyChallengeSelectUiState(
                        challenges = challenges.map { it.toUiModel() }
                    )
                }
                .onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "챌린지를 불러오지 못했어요."
                    )
                }
        }
    }
}

// 챌린지 도메인 모델을 목록 카드와 상세 화면이 공유하는 UI 모델로 변환
private fun PartyChallenge.toUiModel() = PartyChallengeCardUi(
    challenge = PartyChallengeUi(
        id = id,
        title = title,
        category = category,
        participantCount = participantCount,
        summary = summary,
        benefits = benefits,
        recommendations = recommendations,
        verificationInstruction = verificationInstruction,
        verificationImageUrl = verificationImageUrl
    ),
    participantCount = participantCount,
    imageUrl = imageUrl,
    // TODO 실제 API 이미지 로딩 실패 시에만 fallback 리소스 표시
    fallbackImageRes = when (id) {
        "challenge-1" -> R.drawable.party_challenge_morning
        "challenge-2" -> R.drawable.party_challenge_running
        "challenge-3" -> R.drawable.party_challenge_reading
        "challenge-4" -> R.drawable.party_challenge_supplement
        "challenge-5" -> R.drawable.party_challenge_words
        else -> R.drawable.party_challenge_meditation
    }
)

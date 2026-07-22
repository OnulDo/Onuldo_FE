package com.example.onuldo_fe.viewmodel.party

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.onuldo_fe.R
import com.example.onuldo_fe.model.party.PartyChallenge
import com.example.onuldo_fe.repository.party.PartyChallengeRepository
import com.example.onuldo_fe.repository.party.PartyChallengeRepositoryProvider
import com.example.onuldo_fe.ui.screen.party.PartyChallengeCardUi
import com.example.onuldo_fe.ui.screen.party.PartyChallengeUi

class PartyChallengeSelectViewModel(
    private val repository: PartyChallengeRepository = PartyChallengeRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(PartyChallengeSelectUiState())
        private set

    init {
        loadPartyChallenges()
    }

    fun loadPartyChallenges() {
        uiState = PartyChallengeSelectUiState(
            challenges = repository.getPartyChallenges().map { it.toUiModel() }
        )
    }
}

private fun PartyChallenge.toUiModel() = PartyChallengeCardUi(
    challenge = PartyChallengeUi(id, title, period, deposit),
    participantCount = participantCount,
    imageUrl = imageUrl,
    fallbackImageRes = when (id) {
        "challenge-1" -> R.drawable.party_challenge_morning
        "challenge-2" -> R.drawable.party_challenge_running
        "challenge-3" -> R.drawable.party_challenge_reading
        "challenge-4" -> R.drawable.party_challenge_supplement
        "challenge-5" -> R.drawable.party_challenge_words
        else -> R.drawable.party_challenge_meditation
    }
)

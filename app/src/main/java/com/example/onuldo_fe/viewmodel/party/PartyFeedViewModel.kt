package com.example.onuldo_fe.viewmodel.party

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.onuldo_fe.model.party.PartyProgress
import com.example.onuldo_fe.model.party.PartyFeedItem
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.party.PartyFeedItemUi
import com.example.onuldo_fe.repository.party.PartyFeedRepository
import com.example.onuldo_fe.repository.party.PartyFeedRepositoryProvider

data class PartyProgressUiState(
    val progressPercent: Int = 0,
    val completedMemberCount: Int = 0,
    val totalMemberCount: Int = 0
)

data class PartyFeedUiState(
    val partyName: String = "",
    val challengeName: String = "",
    val progress: PartyProgressUiState = PartyProgressUiState(),
    val feedItems: List<PartyFeedItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PartyFeedViewModel(
    private val repository: PartyFeedRepository = PartyFeedRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(PartyFeedUiState())
        private set

    fun loadPartyFeed(partyId: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.getPartyFeed(partyId) }
                .onSuccess { feed ->
                    uiState = PartyFeedUiState(
                        partyName = feed.partyName,
                        challengeName = feed.challengeName,
                        progress = feed.progress.toUiState(),
                        feedItems = feed.items.map { it.toUiState() }
                    )
                }
                .onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "파티 피드를 불러오지 못했어요."
                    )
                }
        }
    }
}

private fun PartyProgress.toUiState() = PartyProgressUiState(
    progressPercent = if (totalMemberCount == 0) {
        0
    } else {
        completedMemberCount * 100 / totalMemberCount
    },
    completedMemberCount = completedMemberCount,
    totalMemberCount = totalMemberCount
)

private fun PartyFeedItem.toUiState() = PartyFeedItemUi(
    memberId = memberId,
    name = nickname,
    time = verifiedElapsedMinutes?.toElapsedTimeText() ?: "미인증",
    profileImageUrl = profileImageUrl,
    verificationImageUrl = verificationImageUrl,
    imageRes = if (verificationImageUrl == null) {
        null
    } else {
        when (memberId) {
            "member-1" -> R.drawable.party_feed_minji
            "member-2" -> R.drawable.party_feed_seoyeon
            "member-3" -> R.drawable.party_feed_jiho
            "member-4" -> R.drawable.party_feed_sua
            else -> null
        }
    }
)

private fun Int.toElapsedTimeText(): String = when {
    this < 1 -> "방금 전"
    this < 60 -> "${this}분 전"
    this < 24 * 60 -> "${this / 60}시간 전"
    else -> "${this / (24 * 60)}일 전"
}

package com.example.onuldo_fe.viewmodel.party

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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
    val progress: PartyProgressUiState = PartyProgressUiState(),
    val feedItems: List<PartyFeedItemUi> = emptyList()
)

class PartyFeedViewModel(
    private val repository: PartyFeedRepository = PartyFeedRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(PartyFeedUiState())
        private set

    init {
        loadPartyFeed("party-001")
    }

    fun loadPartyFeed(partyId: String) {
        // TODO 실제 API 연동 시 선택한 partyId의 팀 진행률 요청
        uiState = PartyFeedUiState(
            progress = repository.getPartyProgress(partyId).toUiState(),
            feedItems = repository.getPartyFeedItems(partyId).map { it.toUiState() }
        )
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
    name = nickname,
    time = verifiedElapsedMinutes?.let { "${it / 60}시간 전" } ?: "미인증",
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

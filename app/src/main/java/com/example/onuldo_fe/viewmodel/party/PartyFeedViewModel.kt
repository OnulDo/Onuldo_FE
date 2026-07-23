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

// 오늘 인증 완료 인원과 전체 인원을 기준으로 계산한 진행 현황
data class PartyProgressUiState(
    val progressPercent: Int = 0,
    val completedMemberCount: Int = 0,
    val totalMemberCount: Int = 0
)

// 파티 피드 헤더·진행률·인증 카드·요청 상태를 한 번에 관리
data class PartyFeedUiState(
    val partyName: String = "",
    val challengeName: String = "",
    val progress: PartyProgressUiState = PartyProgressUiState(),
    val feedItems: List<PartyFeedItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// 선택한 파티의 피드를 불러와 화면에서 바로 사용할 형태로 변환
class PartyFeedViewModel(
    private val repository: PartyFeedRepository = PartyFeedRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(PartyFeedUiState())
        private set

    fun loadPartyFeed(partyId: String) {
        // 파티 카드 선택 또는 재시도 시 해당 partyId의 최신 피드 조회
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

// 명세에 따라 오늘 인증 완료 인원 비율을 정수 퍼센트로 계산
private fun PartyProgress.toUiState() = PartyProgressUiState(
    progressPercent = if (totalMemberCount == 0) {
        0
    } else {
        completedMemberCount * 100 / totalMemberCount
    },
    completedMemberCount = completedMemberCount,
    totalMemberCount = totalMemberCount
)

// 인증하지 않은 파티원도 목록에 유지하고 인증 시간 대신 '미인증' 표시
private fun PartyFeedItem.toUiState() = PartyFeedItemUi(
    memberId = memberId,
    name = nickname,
    time = verifiedElapsedMinutes?.toElapsedTimeText() ?: "미인증",
    profileImageUrl = profileImageUrl,
    verificationImageUrl = verificationImageUrl,
    // 실제 API 이미지가 존재하는 항목에 fake 로컬 이미지를 대응시켜 UI 테스트
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

// 서버가 전달한 인증 후 경과 분을 사용자용 상대 시간 문구로 변환
private fun Int.toElapsedTimeText(): String = when {
    this < 1 -> "방금 전"
    this < 60 -> "${this}분 전"
    this < 24 * 60 -> "${this / 60}시간 전"
    else -> "${this / (24 * 60)}일 전"
}

package com.example.onuldo_fe.viewmodel.party

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.model.party.CreatePartyCommand
import com.example.onuldo_fe.model.party.PartyLifecycleStatus
import com.example.onuldo_fe.model.party.PartyMember
import com.example.onuldo_fe.model.party.PartyMemberReadyStatus
import com.example.onuldo_fe.model.party.PartyRole
import com.example.onuldo_fe.model.party.PartySummary
import com.example.onuldo_fe.model.party.PartyWaitingRoom
import com.example.onuldo_fe.repository.party.PartyRepository
import com.example.onuldo_fe.repository.party.PartyRepositoryProvider
import com.example.onuldo_fe.ui.screen.party.PartyCardUi
import com.example.onuldo_fe.ui.screen.party.PartyMemberRole
import com.example.onuldo_fe.ui.screen.party.PartyMemberUi
import com.example.onuldo_fe.ui.screen.party.PartyReadyStatus
import com.example.onuldo_fe.ui.screen.party.PartyStatus
import com.example.onuldo_fe.ui.screen.party.PartyWaitingRoomUi
import kotlinx.coroutines.launch

enum class PartyAction {
    Idle,
    Creating,
    LoadingRoom,
    ReadySubmitting,
    StartSubmitting,
    LeaveSubmitting
}

data class PartyUiState(
    val parties: List<PartyCardUi> = emptyList(),
    val waitingRoom: PartyWaitingRoomUi? = null,
    val isListLoading: Boolean = false,
    val action: PartyAction = PartyAction.Idle,
    val errorMessage: String? = null
)

class PartyViewModel(
    private val repository: PartyRepository = PartyRepositoryProvider.provide(),
    // TODO 로그인 연동 시 AuthRepository에서 현재 사용자 ID를 받습니다.
    val currentUserId: String = "current-user"
) : ViewModel() {
    var uiState by mutableStateOf(PartyUiState())
        private set

    init {
        loadParties()
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun loadParties() {
        uiState = uiState.copy(isListLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.getParties() }
                .onSuccess { parties ->
                    uiState = uiState.copy(
                        parties = parties.map(PartySummary::toUi),
                        isListLoading = false
                    )
                }
                .onFailure {
                    uiState = uiState.copy(
                        isListLoading = false,
                        errorMessage = "파티 목록을 불러오지 못했어요."
                    )
                }
        }
    }

    fun createParty(command: CreatePartyCommand, onSuccess: (String) -> Unit) {
        // 생성 요청 성공 시 발급된 partyId로 대기방 정보를 조회한 뒤 화면 상태에 반영합니다.
        if (uiState.action != PartyAction.Idle) return
        uiState = uiState.copy(action = PartyAction.Creating, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                val created = repository.createParty(command)
                repository.getWaitingRoom(created.partyId)
            }.onSuccess { room ->
                uiState = uiState.copy(
                    waitingRoom = room.toUi(),
                    action = PartyAction.Idle
                )
                onSuccess(room.partyId)
            }.onFailure {
                uiState = uiState.copy(
                    action = PartyAction.Idle,
                    errorMessage = "파티를 만들지 못했어요."
                )
            }
        }
    }

    fun loadWaitingRoom(partyId: String, onSuccess: () -> Unit = {}) {
        // 방장과 파티원이 동일한 API 응답을 사용해 역할·준비 상태·정원을 표시합니다.
        uiState = uiState.copy(waitingRoom = null, action = PartyAction.LoadingRoom, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.getWaitingRoom(partyId) }
                .onSuccess { room ->
                    uiState = uiState.copy(
                        waitingRoom = room.toUi(),
                        action = PartyAction.Idle
                    )
                    onSuccess()
                }
                .onFailure {
                    uiState = uiState.copy(
                        action = PartyAction.Idle,
                        errorMessage = "파티 대기방을 불러오지 못했어요."
                    )
                }
        }
    }

    fun readyParty() {
        // 준비완료 요청 성공 응답에 포함된 최신 멤버 목록으로 대기방을 갱신합니다.
        val partyId = uiState.waitingRoom?.partyId ?: return
        if (uiState.action != PartyAction.Idle) return
        uiState = uiState.copy(action = PartyAction.ReadySubmitting, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.readyParty(partyId) }
                .onSuccess { room ->
                    uiState = uiState.copy(waitingRoom = room.toUi(), action = PartyAction.Idle)
                }
                .onFailure {
                    uiState = uiState.copy(
                        action = PartyAction.Idle,
                        errorMessage = "준비완료 처리에 실패했어요."
                    )
                }
        }
    }

    fun leaveParty(onSuccess: () -> Unit) {
        // 탈퇴 요청이 성공한 경우에만 대기방 상태를 제거하고 목록 화면으로 이동합니다.
        val partyId = uiState.waitingRoom?.partyId ?: return
        if (uiState.action != PartyAction.Idle) return
        uiState = uiState.copy(action = PartyAction.LeaveSubmitting, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.leaveParty(partyId) }
                .onSuccess {
                    uiState = uiState.copy(waitingRoom = null, action = PartyAction.Idle)
                    onSuccess()
                }
                .onFailure {
                    uiState = uiState.copy(
                        action = PartyAction.Idle,
                        errorMessage = "파티에서 나가지 못했어요."
                    )
                }
        }
    }

    fun startParty(onSuccess: (String) -> Unit) {
        // 시작 요청 중 중복 클릭을 막고 성공 후 진행 중 파티 목록을 다시 조회합니다.
        val partyId = uiState.waitingRoom?.partyId ?: return
        if (uiState.action != PartyAction.Idle) return
        uiState = uiState.copy(action = PartyAction.StartSubmitting, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.startParty(partyId) }
                .onSuccess {
                    uiState = uiState.copy(action = PartyAction.Idle)
                    loadParties()
                    onSuccess(partyId)
                }
                .onFailure {
                    uiState = uiState.copy(
                        action = PartyAction.Idle,
                        errorMessage = "파티를 시작하지 못했어요."
                    )
                }
        }
    }
}

private fun PartyWaitingRoom.toUi() = PartyWaitingRoomUi(
    partyId = partyId,
    partyName = partyName,
    challengeName = challengeName,
    inviteCode = inviteCode,
    period = period,
    deposit = deposit,
    capacity = capacity,
    members = members.map(PartyMember::toUi)
)

private fun PartyMember.toUi() = PartyMemberUi(
    name = nickname,
    role = if (role == PartyRole.Leader) PartyMemberRole.Leader else PartyMemberRole.Member,
    readyStatus = when (readyStatus) {
        PartyMemberReadyStatus.NotApplicable -> PartyReadyStatus.NotApplicable
        PartyMemberReadyStatus.Waiting -> PartyReadyStatus.Waiting
        PartyMemberReadyStatus.Ready -> PartyReadyStatus.Ready
    },
    id = id,
    joinedOrder = joinedOrder,
    profileImageUrl = profileImageUrl
)

private fun PartySummary.toUi() = PartyCardUi(
    id = partyId,
    partyName = partyName,
    challengeName = challengeName,
    dDay = dDay,
    deadline = deadline,
    remainingText = remainingText,
    completedMemberCount = completedMemberCount,
    totalMemberCount = totalMemberCount,
    status = when (status) {
        PartyLifecycleStatus.Recruiting -> PartyStatus.Recruiting
        PartyLifecycleStatus.InProgress -> PartyStatus.InProgress
        PartyLifecycleStatus.Disbanded -> PartyStatus.Disbanded
    }
)

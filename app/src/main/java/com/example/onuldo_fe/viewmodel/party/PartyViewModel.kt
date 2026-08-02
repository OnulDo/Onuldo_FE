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
import kotlinx.coroutines.launch

// 생성·조회·준비·시작·이탈 중 진행 중인 요청을 표시해 중복 실행 방지
enum class PartyAction {
    Idle,
    Creating,
    LoadingRoom,
    ReadySubmitting,
    StartSubmitting,
    LeaveSubmitting
}

// 파티 목록과 대기방이 공유하는 화면 상태
data class PartyUiState(
    val parties: List<PartyCardUi> = emptyList(),        // 파티 홈에 표시할 진행 중인 파티 목록
    val waitingRoom: PartyWaitingRoomUi? = null,         // 현재 입장한 파티의 최신 대기방 정보
    val isReadySubmitted: Boolean = false,               // 현재 화면에서 준비 완료 요청이 성공했는지 여부
    val isListLoading: Boolean = false,                  // 파티 목록을 불러오는 중인지 여부
    val action: PartyAction = PartyAction.Idle,          // 현재 진행 중인 파티 요청
    val errorMessage: String? = null                     // API 요청 실패 시 화면에 표시할 문구
)

// 파티 생성부터 대기방 시작·이탈까지 파티의 핵심 상태 변경 관리
class PartyViewModel(
    private val repository: PartyRepository = PartyRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(PartyUiState())
        private set

    init {
        // 파티 홈 진입 시 모집 중 파티를 제외한 진행 중 파티 목록 준비
        loadParties()
    }

    // 이전 요청의 오류가 다음 화면에 남지 않도록 화면 이동 전 초기화
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun loadParties() {
        // Repository의 도메인 모델을 화면 전용 카드 모델로 변환해 저장
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
        // 생성 성공과 대기방 조회를 분리해 조회 실패 시 파티를 다시 생성하지 않도록 처리
        // action이 Idle이 아니면 연속 클릭에 따른 동일 파티 중복 생성 방지
        if (uiState.action != PartyAction.Idle) return
        uiState = uiState.copy(action = PartyAction.Creating, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.createParty(command) }
                .onSuccess { created ->
                    uiState = uiState.copy(
                        waitingRoom = null,
                        isReadySubmitted = false,
                        action = PartyAction.Idle
                    )
                    onSuccess(created.partyId)
                    loadWaitingRoom(created.partyId)
                }
                .onFailure {
                    uiState = uiState.copy(
                        action = PartyAction.Idle,
                        errorMessage = "파티를 만들지 못했어요."
                    )
                }
        }
    }

    fun loadWaitingRoom(partyId: String, onSuccess: () -> Unit = {}) {
        // 방장과 파티원이 동일한 API 응답을 사용해 역할·준비 상태·정원 표시
        uiState = uiState.copy(
            waitingRoom = null,
            isReadySubmitted = false,
            action = PartyAction.LoadingRoom,
            errorMessage = null
        )
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
        // 준비완료 요청 성공 응답에 포함된 최신 멤버 목록으로 대기방 갱신
        // 포인트 부족 여부는 화면에서 먼저 확인하고 실제 연동 후 서버에서도 최종 검증
        val partyId = uiState.waitingRoom?.partyId ?: return
        if (uiState.action != PartyAction.Idle) return
        uiState = uiState.copy(action = PartyAction.ReadySubmitting, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.readyParty(partyId) }
                .onSuccess { room ->
                    // 현재 화면에서는 준비 완료를 단방향으로 처리해 성공 후 재요청을 막는다.
                    uiState = uiState.copy(
                        waitingRoom = room.toUi(),
                        isReadySubmitted = true,
                        action = PartyAction.Idle
                    )
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
        // 탈퇴 요청 성공 시에만 대기방 상태 제거 후 목록 화면으로 이동
        // 방장 승계와 마지막 인원 이탈에 따른 해체 처리는 서버 또는 fake store가 담당
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
        // 시작 요청 중 중복 클릭 방지 및 성공 후 진행 중 파티 목록 재조회
        // 시작 가능 조건은 버튼 활성화에 사용하고 서버가 동일 조건을 다시 검증
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

// Repository가 전달한 도메인 대기방 모델을 Compose 화면 전용 모델로 변환
private fun PartyWaitingRoom.toUi() = PartyWaitingRoomUi(
    partyId = partyId,
    partyName = partyName,
    inviteCode = inviteCode,
    period = period,
    deposit = deposit,
    capacity = capacity,
    members = members.map(PartyMember::toUi),
    // Repository가 Fake/Real 차이를 통일했으므로 ViewModel은 응답값만 전달한다.
    isHost = isHost,
    canStart = canStart
)

// 서버 문자열 상태가 변환된 도메인 enum을 화면에서 사용하는 enum으로 매핑
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
    profileImageUrl = profileImageUrl,
    defaultCharacterId = defaultCharacterId
)

// 진행 중 파티 요약 정보를 파티 홈 카드에 표시할 UI 모델로 변환
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

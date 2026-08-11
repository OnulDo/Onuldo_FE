package com.example.onuldo_fe.viewmodel.party

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.model.home.ChallengeStatus
import com.example.onuldo_fe.model.party.CreatePartyCommand
import com.example.onuldo_fe.model.party.PartyLifecycleStatus
import com.example.onuldo_fe.model.party.PartyMember
import com.example.onuldo_fe.model.party.PartyMemberReadyStatus
import com.example.onuldo_fe.model.party.PartyRole
import com.example.onuldo_fe.model.party.PartySummary
import com.example.onuldo_fe.model.party.PartyVerificationStatus
import com.example.onuldo_fe.model.party.PartyWaitingRoom
import com.example.onuldo_fe.repository.party.PartyRepository
import com.example.onuldo_fe.repository.party.PartyRepositoryProvider
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.util.Locale

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
    val isReadySubmitted: Boolean = false,               // 로그인 파티원의 현재 준비 상태
    val isListLoading: Boolean = false,                  // 파티 목록을 불러오는 중인지 여부(전체 화면 로딩)
    val isRefreshing: Boolean = false,                    // 당겨서 새로고침 중인지 여부(기존 목록 유지)
    val action: PartyAction = PartyAction.Idle,          // 현재 진행 중인 파티 요청
    val errorMessage: String? = null,                    // API 요청 실패 시 화면에 표시할 문구
    val availablePoint: Int? = null,                     // 실제 지갑 API에서 조회한 보유 포인트
    val isCreatePointInsufficient: Boolean = false,      // 생성 요청에서 서버가 판정한 포인트 부족 여부
    val isReadyPointInsufficient: Boolean = false        // 준비 요청에서 서버가 판정한 포인트 부족 여부
)

// 파티 생성부터 대기방 시작·이탈까지 파티의 핵심 상태 변경 관리
class PartyViewModel(
    private val repository: PartyRepository = PartyRepositoryProvider.provide(),
    private val userRepository: UserRepository = UserRepositoryProvider.provide()
) : ViewModel() {
    companion object {
        private const val WAITING_ROOM_POLLING_INTERVAL_MS = 3_000L
        private const val PARTY_LIST_REFRESH_INDICATOR_MIN_MS = 300L
    }

    // 파티 목록 조회 표시 방식 — 챌린지 목록(ChallengeListViewModel)과 동일한 3단계 구분
    // FULL: 전체 화면 로딩, REFRESH: 당겨서 새로고침 인디케이터, SILENT: 표시 없이 데이터만 갱신
    private enum class PartyListLoadMode { FULL, REFRESH, SILENT }

    var uiState by mutableStateOf(PartyUiState())
        private set

    private var waitingRoomPollingJob: Job? = null
    private var pollingPartyId: String? = null
    private var waitingRoomMutationGeneration: Long = 0L
    private var partyListJob: Job? = null
    private var partyListGeneration: Long = 0L
    private var hasLoadedPartyList: Boolean = false
    private var pointRequestGeneration: Long = 0L
    // 백그라운드 전환으로 자동 이탈을 시도했지만 실패해 아직 해소되지 않은 파티 ID.
    // 화면은 이미 목록으로 돌아갔으므로, 다음에 목록이 다시 보일 때 조용히 재시도한다.
    private var pendingAutoLeavePartyId: String? = null

    init {
        // 파티 목록 로드는 PartyRoute의 화면 표시(ON_START) 옵저버가 onPartyListVisible()로
        // 담당하므로 여기서는 다시 호출하지 않는다.
        refreshAvailablePoint()
    }

    // 이전 요청의 오류가 다음 화면에 남지 않도록 화면 이동 전 초기화
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun dismissCreatePointDialog() {
        uiState = uiState.copy(isCreatePointInsufficient = false)
    }

    fun dismissReadyPointDialog() {
        uiState = uiState.copy(isReadyPointInsufficient = false)
    }

    /** 포인트 충전 후 화면으로 돌아왔을 때 표시 잔액을 최신 지갑 정보로 갱신한다. */
    fun refreshAvailablePoint() {
        // 초기 조회와 충전 후 복귀 시 조회가 겹칠 수 있어, 응답이 도착한 시점의 세대를
        // 확인해 더 최신 요청이 이미 시작된 뒤 도착한 오래된 응답은 반영하지 않는다.
        val requestGeneration = ++pointRequestGeneration
        viewModelScope.launch {
            val point = fetchAvailablePoint()
            if (point != null && requestGeneration == pointRequestGeneration) {
                uiState = uiState.copy(availablePoint = point.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
            }
        }
    }

    /** 지갑 요약을 우선 사용하고, 조회 실패 시 마이페이지의 현재 포인트로 보완한다. */
    private suspend fun fetchAvailablePoint(): Long? =
        userRepository.getWalletSummary().getOrNull()?.balance
            ?: userRepository.getMyPage().getOrNull()?.currentPoint

    fun loadParties() {
        requestPartyList(PartyListLoadMode.FULL)
    }

    /** 파티 목록이 보이는 시점에 최초 조회 또는 조용한 재조회를 시작한다. */
    fun onPartyListVisible() {
        // 백그라운드 전환 때 자동 이탈이 실패해 남아있으면, 목록이 다시 보일 때(=앱이
        // 다시 활성화됐을 때) 조용히 재시도한다.
        retryPendingAutoLeaveIfNeeded()
        // 이미 시작한 조회가 있으면 초기 진입·ON_START 이벤트의 중복 요청을 막는다.
        if (partyListJob?.isActive == true) return
        requestPartyList(if (hasLoadedPartyList) PartyListLoadMode.SILENT else PartyListLoadMode.FULL)
    }

    /** 당겨서 새로고침 제스처에서 호출: 상단 인디케이터를 표시하며 재조회한다. */
    fun refreshParties() {
        // 챌린지 목록과 동일하게, 이미 조회 중이면 중복 요청을 무시한다.
        if (uiState.isListLoading || uiState.isRefreshing) return
        requestPartyList(PartyListLoadMode.REFRESH)
    }

    private fun requestPartyList(mode: PartyListLoadMode) {
        val generation = ++partyListGeneration
        partyListJob?.cancel()

        // 당겨서 새로고침 인디케이터 최소 표시 시간 측정용
        val refreshStartTime = if (mode == PartyListLoadMode.REFRESH) System.currentTimeMillis() else 0L

        // 표시 상태 설정: FULL=전체 로딩, REFRESH=상단 인디케이터, SILENT=아무 표시 없음(기존 목록 유지)
        // FULL은 진행 중이던 새로고침 인디케이터를 함께 해제해 두 인디케이터가 겹쳐 보이지 않게 한다.
        uiState = when (mode) {
            PartyListLoadMode.FULL -> uiState.copy(isListLoading = true, isRefreshing = false, errorMessage = null)
            PartyListLoadMode.REFRESH -> uiState.copy(isRefreshing = true, errorMessage = null)
            PartyListLoadMode.SILENT -> uiState.copy(errorMessage = null)
        }

        partyListJob = viewModelScope.launch {
            try {
                val parties = repository.getParties()
                if (generation != partyListGeneration) return@launch

                // 당겨서 새로고침일 때만, 응답이 너무 빨라 인디케이터가 깜빡이지 않도록 최소 시간 유지
                if (mode == PartyListLoadMode.REFRESH) {
                    val elapsed = System.currentTimeMillis() - refreshStartTime
                    if (elapsed < PARTY_LIST_REFRESH_INDICATOR_MIN_MS) {
                        delay(PARTY_LIST_REFRESH_INDICATOR_MIN_MS - elapsed)
                    }
                }

                hasLoadedPartyList = true
                uiState = uiState.copy(
                    parties = parties.map(PartySummary::toUi),
                    isListLoading = false,
                    isRefreshing = false
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(PARTY_VIEW_MODEL_TAG, "파티 목록 조회 실패", error)
                if (generation != partyListGeneration) return@launch
                uiState = uiState.copy(
                    isListLoading = false,
                    isRefreshing = false,
                    // 재진입/조용한 갱신 실패는 기존 목록을 유지하고 최초 조회 실패만 오류로 표시한다.
                    errorMessage = "파티 목록을 불러오지 못했어요."
                        .takeUnless { hasLoadedPartyList }
                )
            }
        }
    }

    fun createParty(command: CreatePartyCommand, onSuccess: (String) -> Unit) {
        // 생성 성공과 대기방 조회를 분리해 조회 실패 시 파티를 다시 생성하지 않도록 처리
        // action이 Idle이 아니면 연속 클릭에 따른 동일 파티 중복 생성 방지
        if (uiState.action != PartyAction.Idle) return
        uiState = uiState.copy(
            action = PartyAction.Creating,
            errorMessage = null,
            isCreatePointInsufficient = false
        )
        viewModelScope.launch {
            // 생성 직전에 최신 잔액을 다시 확인해 API 오류 코드와 무관하게 부족 모달을 표시한다.
            fetchAvailablePoint()?.let { point ->
                val availablePoint = point.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                uiState = uiState.copy(availablePoint = availablePoint)
                if (availablePoint < command.deposit) {
                    uiState = uiState.copy(
                        action = PartyAction.Idle,
                        isCreatePointInsufficient = true
                    )
                    return@launch
                }
            }

            try {
                val created = repository.createParty(command)
                uiState = uiState.copy(
                    waitingRoom = null,
                    isReadySubmitted = false,
                    action = PartyAction.Idle
                )
                onSuccess(created.partyId)
                loadWaitingRoom(created.partyId)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val serverError = error.toPartyServerError()
                val isPointInsufficient = serverError.isInsufficientPartyPoint()
                uiState = uiState.copy(
                    action = PartyAction.Idle,
                    isCreatePointInsufficient = isPointInsufficient,
                    errorMessage = when {
                        isPointInsufficient -> null
                        serverError.isAlreadyParticipatingChallenge() -> "이미 진행 중인 챌린지가 있습니다."
                        else -> "파티를 만들지 못했어요."
                    }
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

    /** 대기방에 머무는 동안 3초마다 최신 멤버와 준비 상태를 조회한다. */
    fun startWaitingRoomPolling(partyId: String) {
        if (waitingRoomPollingJob?.isActive == true && pollingPartyId == partyId) return

        stopWaitingRoomPolling()
        pollingPartyId = partyId
        waitingRoomPollingJob = viewModelScope.launch {
            while (isActive) {
                refreshWaitingRoomSilently(partyId)
                delay(WAITING_ROOM_POLLING_INTERVAL_MS)
            }
        }
    }

    /** 대기방을 벗어나거나 앱이 백그라운드로 가면 불필요한 요청을 중지한다. */
    fun stopWaitingRoomPolling() {
        waitingRoomPollingJob?.cancel()
        waitingRoomPollingJob = null
        pollingPartyId = null
    }

    /**
     * 다른 사용자(방장)가 파티를 시작해 폴링으로 이를 감지하고 홈으로 이동할 때 호출한다.
     * leaveParty와 달리 나는 여전히 파티원이므로 이탈 API는 보내지 않고, 더 이상 대기방이
     * 아닌 로컬 캐시만 비운다. 비워두지 않으면 startParty()와 동일하게, 하단 탭 전환 뒤 파티
     * 탭으로 돌아왔을 때 이미 시작된 파티의 낡은 대기방 화면·폴링이 되살아난다.
     */
    fun clearWaitingRoomAfterStart() {
        stopWaitingRoomPolling()
        uiState = uiState.copy(waitingRoom = null)
    }

    private suspend fun refreshWaitingRoomSilently(partyId: String) {
        // 요청 시작 시점의 세대를 저장해 이후 상태 변경보다 오래된 응답인지 판별한다.
        val requestGeneration = waitingRoomMutationGeneration
        try {
            val room = repository.getWaitingRoom(partyId)
            // 버튼 요청 중 받은 오래된 응답이 준비·시작 결과를 덮지 않게 한다.
            if (
                pollingPartyId == partyId &&
                uiState.action == PartyAction.Idle &&
                requestGeneration == waitingRoomMutationGeneration
            ) {
                uiState = uiState.copy(waitingRoom = room.toUi())
            }
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            // 기존 화면을 유지하고 다음 주기에 다시 조회한다.
        }
    }

    /** 초대코드 참여 응답의 최신 대기방을 추가 조회 없이 화면 상태에 적용한다. */
    fun applyJoinedWaitingRoom(room: PartyWaitingRoom) {
        waitingRoomMutationGeneration++
        uiState = uiState.copy(
            waitingRoom = room.toUi(),
            isReadySubmitted = false,
            action = PartyAction.Idle,
            errorMessage = null
        )
    }

    fun readyParty() {
        // 준비완료 요청 성공 응답에 포함된 최신 멤버 목록으로 대기방 갱신
        // 포인트 부족 여부는 화면에서 먼저 확인하고 실제 연동 후 서버에서도 최종 검증
        val partyId = uiState.waitingRoom?.partyId ?: return
        if (uiState.action != PartyAction.Idle) return
        val targetReady = !uiState.isReadySubmitted
        waitingRoomMutationGeneration++
        uiState = uiState.copy(
            action = PartyAction.ReadySubmitting,
            errorMessage = null,
            isReadyPointInsufficient = false
        )
        viewModelScope.launch {
            // READY로 바꿀 때만 최신 잔액을 확인하고, WAITING 복귀는 포인트 검사 없이 요청한다.
            if (targetReady) {
                fetchAvailablePoint()?.let { point ->
                    val availablePoint = point.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                    uiState = uiState.copy(availablePoint = availablePoint)
                    val requiredPoint = uiState.waitingRoom?.deposit ?: return@launch
                    if (availablePoint < requiredPoint) {
                        uiState = uiState.copy(
                            action = PartyAction.Idle,
                            isReadyPointInsufficient = true
                        )
                        return@launch
                    }
                }
            }

            try {
                val room = repository.readyParty(partyId, ready = targetReady)
                // 요청할 때 정한 목표 상태를 그대로 반영한다.
                uiState = uiState.copy(
                    waitingRoom = room.toUi(),
                    isReadySubmitted = targetReady,
                    action = PartyAction.Idle
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val serverError = error.toPartyServerError()
                val isPointInsufficient = serverError.isInsufficientPartyPoint()
                uiState = uiState.copy(
                    action = PartyAction.Idle,
                    isReadyPointInsufficient = isPointInsufficient,
                    errorMessage = if (isPointInsufficient) null else "준비완료 처리에 실패했어요."
                )
            }
        }
    }

    fun leaveParty(onSuccess: () -> Unit) {
        // 탈퇴 요청 성공 시에만 대기방 상태 제거 후 목록 화면으로 이동
        // 방장 승계와 마지막 인원 이탈에 따른 해체 처리는 서버 또는 fake store가 담당
        val partyId = uiState.waitingRoom?.partyId ?: return
        attemptOrQueueLeave(partyId, onSuccess)
    }

    /**
     * 대기방을 벗어나며 앱이 백그라운드로 갈 때 호출. 화면은 호출 시점에 이미 목록으로
     * 돌아간 상태이므로 여기서는 실제 서버 이탈만 담당한다. 실패하면(네트워크 문제 등)
     * pendingAutoLeavePartyId에 의도를 남겨두고, 목록이 다시 보이는 시점(onPartyListVisible)에
     * 재시도한다 — 그렇지 않으면 사용자가 서버의 대기방 멤버로 남았는데도 되돌아갈 UI 경로가 없다.
     */
    fun autoLeaveOnBackground(partyId: String) {
        pendingAutoLeavePartyId = partyId
        attemptOrQueueLeave(partyId) {
            if (pendingAutoLeavePartyId == partyId) pendingAutoLeavePartyId = null
        }
    }

    /** 실패한 자동 이탈이 남아있고 여전히 같은 파티의 대기방이면 조용히 재시도한다. */
    private fun retryPendingAutoLeaveIfNeeded() {
        val partyId = pendingAutoLeavePartyId ?: return
        if (uiState.waitingRoom?.partyId?.let { it != partyId } == true) {
            // 이미 다른 경로(수동 이탈, 파티 해체 등)로 해소됨
            pendingAutoLeavePartyId = null
            return
        }
        attemptOrQueueLeave(partyId) {
            if (pendingAutoLeavePartyId == partyId) pendingAutoLeavePartyId = null
        }
    }

    private fun attemptOrQueueLeave(partyId: String, onSuccess: () -> Unit) {
        if (uiState.action != PartyAction.Idle) {
            // 준비완료·시작하기 등 다른 요청이 이미 진행 중이면 여기서 그냥 포기하지 않고,
            // 그 요청이 끝날 때까지 기다렸다가 그때도 여전히 같은 파티의 대기방이면 이탈을
            // 다시 시도한다. 그냥 버리면 화면은 벗어났는데 서버 멤버십·waitingRoom은
            // 그대로 남는 상태가 된다.
            viewModelScope.launch {
                snapshotFlow { uiState.action }.first { it == PartyAction.Idle }
                if (uiState.waitingRoom?.partyId?.let { it != partyId } != true) {
                    performLeaveParty(partyId, onSuccess)
                }
            }
            return
        }
        performLeaveParty(partyId, onSuccess)
    }

    private fun performLeaveParty(partyId: String, onSuccess: () -> Unit) {
        waitingRoomMutationGeneration++
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
        waitingRoomMutationGeneration++
        uiState = uiState.copy(action = PartyAction.StartSubmitting, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.startParty(partyId) }
                .onSuccess {
                    // 시작한 파티는 더 이상 대기방이 아니므로 leaveParty와 동일하게 비워준다.
                    // 비워두지 않으면 하단 탭 전환(saveState/restoreState) 뒤 파티 탭으로
                    // 돌아왔을 때 이미 시작된 파티의 낡은 대기방 화면·폴링이 되살아난다.
                    stopWaitingRoomPolling()
                    uiState = uiState.copy(action = PartyAction.Idle, waitingRoom = null)
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

private data class PartyServerError(
    val code: String = "",
    val message: String = ""
)

/** 서버 에러 응답 바디는 한 번만 읽을 수 있으므로 code/message를 함께 꺼내 재사용한다. */
private fun Throwable.toPartyServerError(): PartyServerError {
    if (this !is HttpException) return PartyServerError()
    val body = runCatching { response()?.errorBody()?.string() }.getOrNull().orEmpty()
    val errorBody = runCatching { JSONObject(body) }.getOrNull()
    return PartyServerError(
        code = errorBody?.optString("code").orEmpty(),
        message = errorBody?.optString("message").orEmpty()
    )
}

/** 파티 생성/준비 요청 실패 응답에서 서버의 포인트 부족 코드를 확인한다. */
private fun PartyServerError.isInsufficientPartyPoint(): Boolean {
    return code.contains("INSUFFICIENT_POINT") ||
        code == "PAR-ERR-03" ||
        message.contains("포인트") && message.contains("부족")
}

/** 이미 개인 챌린지에 참여 중이면 파티 생성도 막히므로 전용 안내 문구를 보여준다. */
private fun PartyServerError.isAlreadyParticipatingChallenge(): Boolean {
    val codeText = code.uppercase(Locale.ROOT)
    return codeText.contains("CHALLENGE") &&
        (codeText.contains("ALREADY") || codeText.contains("ONGOING") || codeText.contains("IN_PROGRESS")) ||
        message.contains("이미") && message.contains("챌린지") &&
        (message.contains("진행") || message.contains("참여"))
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
    canStart = canStart,
    status = when (status) {
        PartyLifecycleStatus.Recruiting -> PartyStatus.Recruiting
        PartyLifecycleStatus.InProgress -> PartyStatus.InProgress
        PartyLifecycleStatus.Disbanded -> PartyStatus.Disbanded
    }
)

private const val PARTY_VIEW_MODEL_TAG = "PartyViewModel"

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
    challengeId = challengeId,
    partyName = partyName,
    challengeName = challengeName,
    dDay = dDay,
    deadline = deadline,
    remainingText = remainingText,
    completedMemberCount = completedMemberCount,
    totalMemberCount = totalMemberCount,
    goal = goal,
    verificationStatus = when (verificationStatus) {
        PartyVerificationStatus.NotVerified -> ChallengeStatus.NeedCertification
        PartyVerificationStatus.Pending -> ChallengeStatus.WaitingReview
        PartyVerificationStatus.Success -> ChallengeStatus.Success
        PartyVerificationStatus.Fail -> ChallengeStatus.Failed
    },
    myDailyStatus = myDailyStatus,
    members = members.map { member ->
        PartyCardMemberUi(
            userId = member.userId,
            nickname = member.nickname,
            profileImageUrl = member.profileImageUrl,
            isVerifiedToday = member.isVerifiedToday
        )
    },
    status = when (status) {
        PartyLifecycleStatus.Recruiting -> PartyStatus.Recruiting
        PartyLifecycleStatus.InProgress -> PartyStatus.InProgress
        PartyLifecycleStatus.Disbanded -> PartyStatus.Disbanded
    }
)

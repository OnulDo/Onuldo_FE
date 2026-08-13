package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.data.notification.dto.NotificationSettingType
import com.example.onuldo_fe.model.notification.NotificationSettings
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import com.example.onuldo_fe.ui.screen.mypage.NotificationSettingsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 마이 - 알림 설정.
 *
 * "전체 알림 수신" 토글은 서버 `PATCH`에 `type=ALL_ENABLED` 한 건만 보낸다 — true면 서버가
 * 개별 6종을 전부 켜고, false면 [SETTLEMENT_COMPLETE](정산/환급, 필수 알림 정책)를 제외한
 * 나머지를 끈다. 개별 항목을 바꿀 때는 마스터 스위치를 건드리지 않는다(화면이 마스터로
 * 개별 토글을 잠그기 때문).
 *
 * 화면은 기존 [NotificationSettingsState]를 그대로 쓰고, 여기서 서버 모델과 상호 변환한다.
 */
class NotificationSettingsViewModel(
    private val userRepository: UserRepository = UserRepositoryProvider.provide(),
    /** 테스트에서 스코프를 갈아끼우기 위한 것. 앱에서는 null이라 [viewModelScope]를 쓴다. */
    private val coroutineScope: CoroutineScope? = null,
) : ViewModel() {

    private val scope: CoroutineScope get() = coroutineScope ?: viewModelScope

    private val _state = MutableStateFlow(NotificationSettings.EMPTY.toUiState())
    val state: StateFlow<NotificationSettingsState> = _state.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * 저장 요청을 한 줄로 세운다.
     *
     * [apply]는 호출될 때마다 코루틴을 새로 띄우므로, 같은 항목을 빠르게 두 번 토글하면
     * 두 PATCH가 동시에 날아가 **도착 순서가 뒤바뀔 수 있다.** 끄기→켜기를 연달아 눌렀는데
     * 끄기가 나중에 도착하면 서버는 꺼짐, 화면은 켜짐으로 갈린다.
     */
    private val applyMutex = Mutex()

    /**
     * 가장 마지막 [apply] 호출 번호. 실패 후 서버 값을 다시 읽을 때, 그사이 사용자가 다른
     * 토글을 눌렀다면 재조회 결과가 그 변경을 덮어쓰지 않도록 이 번호로 걸러낸다.
     *
     * 증가는 [apply](UI 스레드)에서만 일어나므로 원자적 연산은 필요 없지만, 코루틴이 다른
     * 디스패처에서 읽을 수 있어 가시성만 [Volatile]로 보장한다.
     */
    @Volatile
    private var applyGeneration = 0

    init {
        load()
    }

    fun load() {
        scope.launch { refreshFromServer() }
    }

    /** 서버 값을 읽어 화면에 반영한다. 순서를 지켜야 하는 자리에서 직접 부를 수 있도록 suspend로 둔다. */
    private suspend fun refreshFromServer() {
        userRepository.getNotificationSettings()
            .onSuccess { settings -> _state.value = settings.toUiState() }
            .onError { _, message -> _errorMessage.value = message }
    }

    /**
     * 저장 실패 후 서버 값으로 화면을 맞춘다.
     *
     * **조회 결과를 반영하는 시점에 [generation]을 다시 확인한다.** 조회를 시작할 때만 확인하면,
     * 응답을 기다리는 사이 사용자가 누른 토글을 옛 서버 값이 덮어쓴다([apply]는 뮤텍스 밖에서
     * 화면을 즉시 갱신하므로 조회 중에도 상태가 바뀔 수 있다).
     *
     * 조회가 실패하면 화면을 그대로 두고 [_errorMessage]도 건드리지 않는다.
     * 사용자에게 중요한 건 조회 실패가 아니라 앞서 담아 둔 **저장 실패** 문구다.
     */
    private suspend fun reconcileWithServer(generation: Int) {
        userRepository.getNotificationSettings()
            .onSuccess { settings ->
                if (generation == applyGeneration) _state.value = settings.toUiState()
            }
    }

    /**
     * 화면에서 만든 새 상태를 반영한다.
     *
     * 이전 상태와 비교해 **바뀐 항목만** 서버에 PATCH한다. 응답을 기다리지 않고 화면을 먼저
     * 갱신(낙관적 업데이트)하고, 실패하면 남은 요청을 보내지 않은 뒤 서버 값을 다시 읽어 맞춘다.
     */
    fun apply(newState: NotificationSettingsState) {
        val previous = _state.value
        if (previous == newState) return

        _state.value = newState

        val changes: List<Pair<NotificationSettingType, Boolean>> =
            if (previous.all != newState.all) {
                listOf(NotificationSettingType.ALL_ENABLED to newState.all)
            } else {
                buildList {
                    if (previous.challengeStart != newState.challengeStart) {
                        add(NotificationSettingType.CHALLENGE_START to newState.challengeStart)
                    }
                    if (previous.deadline != newState.deadline) {
                        add(NotificationSettingType.VERIFICATION_DEADLINE to newState.deadline)
                    }
                    if (previous.endReminder != newState.endReminder) {
                        add(NotificationSettingType.CHALLENGE_END_REMINDER to newState.endReminder)
                    }
                    if (previous.result != newState.result) {
                        add(NotificationSettingType.VERIFICATION_RESULT to newState.result)
                    }
                    if (previous.partyMemberVerified != newState.partyMemberVerified) {
                        add(NotificationSettingType.PARTY_MEMBER_VERIFIED to newState.partyMemberVerified)
                    }
                    if (previous.refund != newState.refund) {
                        add(NotificationSettingType.SETTLEMENT_COMPLETE to newState.refund)
                    }
                }
            }

        if (changes.isEmpty()) return

        val generation = ++applyGeneration

        scope.launch {
            // 앞선 저장이 끝난 뒤에 보낸다. 그래야 같은 항목을 연달아 토글해도
            // 마지막에 누른 값이 서버에 마지막으로 남는다.
            applyMutex.withLock {
                var failure: String? = null

                for ((type, enabled) in changes) {
                    failure = userRepository.updateNotificationSetting(type, enabled)
                        .errorMessageOrNull()
                    if (failure != null) break
                }

                if (failure != null) {
                    _errorMessage.value = failure

                    // 실패 이전 항목은 이미 저장됐을 수 있어, 화면을 [previous]로 되돌리면
                    // 그것대로 서버와 달라진다. 서버 값을 다시 읽어 실제 상태에 맞춘다.
                    //
                    // 단, 그사이 사용자가 다른 토글을 눌렀다면 그쪽이 최종 상태를 정해야 한다.
                    // 여기서 읽어온 값은 그 변경을 모르므로 덮어쓰면 방금 누른 것이 되돌아간다.
                    // (조회 도중에 눌린 경우까지 막으려면 반영 시점에도 확인해야 한다 —
                    //  그 확인은 reconcileWithServer 안에 있다. 여기 확인은 불필요한 조회를 아끼는 용도다.)
                    if (generation == applyGeneration) reconcileWithServer(generation)
                }
            }
        }
    }

    fun consumeError() = _errorMessage.update { null }
}

/**
 * 서버 값을 화면 상태로 옮긴다.
 *
 * 마스터 스위치는 서버 `allEnabled` 값을 그대로 표시한다.
 */
private fun NotificationSettings.toUiState() = NotificationSettingsState(
    all = allEnabled,
    challengeStart = challengeStart,
    deadline = verificationDeadline,
    endReminder = challengeEndReminder,
    result = verificationResult,
    partyMemberVerified = partyMemberVerified,
    refund = settlementComplete,
)

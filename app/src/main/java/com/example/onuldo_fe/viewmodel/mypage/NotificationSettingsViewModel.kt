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
 * 서버 `PATCH`는 개별 항목 하나씩만 받고 "전체" 타입이 없다. 그래서 "전체 알림 수신" 토글은
 * 6종을 모두 같은 값으로 바꾸는 것으로 구현한다("모든 알림을 한 번에 끄거나 켤 수 있어요" 문구와 일치).
 * 개별 항목을 바꿀 때는 마스터 스위치를 건드리지 않는다(화면이 마스터로 개별 토글을 잠그기 때문).
 *
 * ⚠️ 마스터 스위치를 끈 상태는 서버에 저장되지 않는다 — [NotificationSettings] 주석 참고.
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
     */
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
                // 전체 토글: 개별 6종을 모두 같은 값으로 맞춘다.
                NotificationSettingType.entries.map { it to newState.all }
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
                    // 첫 실패에서 멈춘다. 그대로 밀어붙이면 화면은 실패로 처리했는데 남은 항목은
                    // 새 값으로 저장돼 화면과 서버가 어긋난다. 전체 토글은 한 번에 6건을 보내므로
                    // 중간에 실패하면 이 어긋남이 그대로 남는다.
                    if (failure != null) break
                }

                if (failure != null) {
                    _errorMessage.value = failure

                    // 실패 이전 항목은 이미 저장됐을 수 있어, 화면을 [previous]로 되돌리면
                    // 그것대로 서버와 달라진다. 서버 값을 다시 읽어 실제 상태에 맞춘다.
                    //
                    // 단, 그사이 사용자가 다른 토글을 눌렀다면 그쪽이 최종 상태를 정해야 한다.
                    // 여기서 읽어온 값은 그 변경을 모르므로 덮어쓰면 방금 누른 것이 되돌아간다.
                    if (generation == applyGeneration) refreshFromServer()
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
 * (저장 쪽은 `PATCH`에 `ALL` 타입이 없어, 마스터 토글 시 [apply]에서 개별 6종을 일괄로 바꾼다.
 *  서버가 그에 맞춰 `allEnabled`를 재계산해 내려주지 않으면 재진입 시 표시가 어긋날 수 있다.)
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

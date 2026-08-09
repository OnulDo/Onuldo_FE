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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationSettings.EMPTY.toUiState())
    val state: StateFlow<NotificationSettingsState> = _state.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            userRepository.getNotificationSettings()
                .onSuccess { settings -> _state.value = settings.toUiState() }
                .onError { _, message -> _errorMessage.value = message }
        }
    }

    /**
     * 화면에서 만든 새 상태를 반영한다.
     *
     * 이전 상태와 비교해 **바뀐 항목만** 서버에 PATCH한다. 응답을 기다리지 않고 화면을 먼저
     * 갱신(낙관적 업데이트)하고, 실패하면 이전 값으로 되돌린다.
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

        viewModelScope.launch {
            changes.forEach { (type, enabled) ->
                userRepository.updateNotificationSetting(type, enabled)
                    .onError { _, message ->
                        _errorMessage.value = message
                        _state.value = previous // 실패 시 되돌림
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

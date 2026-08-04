package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.data.user.dto.NotificationSettingType
import com.example.onuldo_fe.model.user.NotificationSettings
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
 * 서버는 `allEnabled`를 개별 5종에서 **계산해 내려주는 읽기 전용 값**이고,
 * `PATCH`는 개별 항목 하나씩만 받는다. 그래서 "전체 알림 수신" 토글은 5종을 모두 같은 값으로
 * 바꾸는 것으로 구현한다("모든 알림을 한 번에 끄거나 켤 수 있어요" 문구와도 일치).
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
                // 전체 토글: 개별 5종을 모두 같은 값으로 맞춘다.
                NotificationSettingType.entries.map { it to newState.all }
            } else {
                buildList {
                    if (previous.challengeStart != newState.challengeStart) {
                        add(NotificationSettingType.CHALLENGE_START to newState.challengeStart)
                    }
                    if (previous.deadline != newState.deadline) {
                        add(NotificationSettingType.VERIFICATION_DEADLINE to newState.deadline)
                    }
                    if (previous.result != newState.result) {
                        add(NotificationSettingType.VERIFICATION_RESULT to newState.result)
                    }
                    if (previous.refund != newState.refund) {
                        add(NotificationSettingType.REFUND_COMPLETE to newState.refund)
                    }
                    if (previous.deduction != newState.deduction) {
                        add(NotificationSettingType.DEDUCTION_ALERT to newState.deduction)
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

/** 전체 토글이 켜지면 개별 5종도 모두 켜진 것으로 본다(서버가 allEnabled를 그렇게 계산한다). */
private fun NotificationSettings.toUiState() = NotificationSettingsState(
    all = allEnabled,
    challengeStart = challengeStart,
    deadline = verificationDeadline,
    result = verificationResult,
    refund = refundComplete,
    deduction = deductionAlert,
)

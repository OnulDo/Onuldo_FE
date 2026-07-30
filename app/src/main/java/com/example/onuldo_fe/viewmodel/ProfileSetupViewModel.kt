package com.example.onuldo_fe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.onuldo_fe.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 프로필 설정(온보딩 4/4) 상태. 닉네임 실시간 유효성 + 선택한 캐릭터 인덱스.
 * 캐릭터 실제 drawable 목록은 UI 레이어(ProfileSetupScreen)가 인덱스로 매핑한다.
 */
data class ProfileSetupUiState(
    val nickname: String = "",
    // null = 아직 캐릭터 미선택(디자인: 점선 빈 원 + "+"). 선택 시 인덱스 지정.
    val selectedCharacterIndex: Int? = null,
) {
    private val nicknameErrorMsg get() = Validators.nicknameErrorMessage(nickname)
    private val nicknameValid get() = nickname.isNotEmpty() && nicknameErrorMsg == null

    val nicknameError get() = nicknameErrorMsg != null
    val nicknameSupport: String? get() = nicknameErrorMsg

    // 설계서(ONBD_PROFILE_001): 캐릭터 선택 + 닉네임 유효 모두 충족 시 "계속" 활성화.
    val isContinueEnabled: Boolean get() = nicknameValid && selectedCharacterIndex != null
}

class ProfileSetupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    fun onNicknameChange(value: String) = _uiState.update { it.copy(nickname = value) }

    fun onCharacterSelect(index: Int) = _uiState.update { it.copy(selectedCharacterIndex = index) }

    /**
     * 프로필 설정 완료. 닉네임 유효 시 [onDone] 호출.
     * TODO: 서버 닉네임 중복확인/프로필 저장 API 연동("이미 사용 중인 닉네임이에요" 처리 포함).
     */
    fun submit(onDone: () -> Unit) {
        if (_uiState.value.isContinueEnabled) onDone()
    }
}

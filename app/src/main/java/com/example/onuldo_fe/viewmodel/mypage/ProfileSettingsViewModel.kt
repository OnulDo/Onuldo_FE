package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.model.user.UserProfile
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import com.example.onuldo_fe.utils.ProfileAsset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileSettingsUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val nickname: String get() = profile?.nickname.orEmpty()
    val email: String get() = profile?.email.orEmpty()

    /**
     * 서버 `profileImageUrl`에 대응하는 캐릭터 인덱스. 매칭되지 않으면 null이라
     * 화면은 기본 아바타를 보여준다.
     */
    val characterIndex: Int? get() = ProfileAsset.toCharacterIndex(profile?.profileImageUrl)
}

/**
 * 프로필 설정(마이 진입). `GET /api/users/me/profile`로 표시값을 채운다.
 *
 * 닉네임 변경은 `NicknameEditScreen`이 `PATCH /api/users/me/profile`로 저장하며,
 * 이 화면은 변경 후 복귀 시 `RefreshOnResume`으로 재조회해 최신 값을 반영한다.
 * (아바타 변경은 선택 피커 UI가 아직 없어 저장 흐름이 없다.)
 */
class ProfileSettingsViewModel(
    private val userRepository: UserRepository = UserRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSettingsUiState())
    val uiState: StateFlow<ProfileSettingsUiState> = _uiState.asStateFlow()

    // 최초 표시와 변경 후 복귀 모두 화면의 RefreshOnResume이 load()를 부른다(그래서 init에서 조회하지 않는다).
    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.getProfile()
                .onSuccess { profile ->
                    _uiState.update { it.copy(isLoading = false, profile = profile) }
                }
                .onError { _, message ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
        }
    }
}

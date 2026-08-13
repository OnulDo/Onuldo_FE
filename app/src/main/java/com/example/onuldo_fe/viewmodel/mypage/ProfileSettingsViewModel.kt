package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.ApiErrorCode
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.model.user.UserProfile
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import com.example.onuldo_fe.utils.ProfileAsset
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileSettingsUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** 아바타(캐릭터) 변경 PATCH 진행 중 여부. 피커에서 다른 항목을 다시 누르지 못하게 막는다. */
    val isSavingAvatar: Boolean = false,
    /** 아바타 변경 실패 안내. 닉네임 변경과 분리해, 조회 실패(errorMessage)와 섞이지 않게 한다. */
    val avatarErrorMessage: String? = null,
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
 * 닉네임 변경은 `NicknameEditScreen`이, 아바타 변경은 이 화면의 편집 배지(→ `CharacterPickerSheet`)가
 * 각각 `PATCH /api/users/me/profile`로 저장한다. 닉네임 변경 후 복귀 시에는 `RefreshOnResume`으로
 * 재조회해 최신 값을 반영하고, 아바타 변경은 같은 화면에서 바로 응답을 반영한다.
 */
class ProfileSettingsViewModel(
    private val userRepository: UserRepository = UserRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSettingsUiState())
    val uiState: StateFlow<ProfileSettingsUiState> = _uiState.asStateFlow()

    // 진행 중인 프로필 재조회. 아바타 저장(PATCH)이 이 조회보다 먼저 끝나면,
    // 늦게 도착한 조회 응답이 저장 결과를 덮어쓰지 않도록 취소한다.
    private var loadJob: Job? = null

    // 최초 표시와 변경 후 복귀 모두 화면의 RefreshOnResume이 load()를 부른다(그래서 init에서 조회하지 않는다).
    fun load() {
        // 화면이 보일 때마다 불릴 수 있어, 이전 조회가 아직 돌고 있으면 끊는다.
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
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

    /**
     * 캐릭터 그리드에서 [characterIndex]를 선택했을 때 호출. [ProfileAsset.fromCharacterIndex]로
     * 서버 프리셋 URL을 만들어 `profileImageUrl`만 채워 보낸다(닉네임은 건드리지 않음).
     * 성공하면 응답 프로필로 화면을 즉시 갱신하고 [onSuccess]로 피커를 닫는다.
     */
    fun updateAvatar(characterIndex: Int, onSuccess: () -> Unit) {
        if (_uiState.value.isSavingAvatar) return
        // 진행 중인 재조회(GET)가 늦게 끝나 이 저장 결과를 덮어쓰지 않도록 먼저 끊는다.
        loadJob?.cancel()
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingAvatar = true, avatarErrorMessage = null) }

            val profileImageUrl = ProfileAsset.fromCharacterIndex(characterIndex)
            userRepository.updateProfile(profileImageUrl = profileImageUrl)
                .onSuccess { profile ->
                    _uiState.update { it.copy(isSavingAvatar = false, profile = profile) }
                    onSuccess()
                }
                .onError { code, message ->
                    // 토큰 만료 등 인증 오류는 세션 만료 흐름(→ 랜딩)이 처리하므로 필드 에러로 노출하지 않는다.
                    val display = if (ApiErrorCode.isTokenInvalid(code)) null else message.ifBlank { DEFAULT_AVATAR_ERROR }
                    _uiState.update { it.copy(isSavingAvatar = false, avatarErrorMessage = display) }
                }
        }
    }

    private companion object {
        const val DEFAULT_AVATAR_ERROR = "프로필 사진 변경에 실패했어요. 잠시 후 다시 시도해주세요."
    }
}

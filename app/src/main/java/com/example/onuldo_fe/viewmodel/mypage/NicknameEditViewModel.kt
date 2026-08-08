package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.ApiErrorCode
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NicknameEditUiState(
    val isSubmitting: Boolean = false,
    /** 변경 실패 안내(예: "이미 사용 중인 닉네임이에요"). 서버 문구를 그대로 노출한다. */
    val errorMessage: String? = null,
)

/** 닉네임 변경. `PATCH /api/users/me/profile`로 nickname만 채워 보낸다. */
class NicknameEditViewModel(
    private val userRepository: UserRepository = UserRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(NicknameEditUiState())
    val uiState: StateFlow<NicknameEditUiState> = _uiState.asStateFlow()

    /**
     * 닉네임 변경 요청. 성공한 경우에만 [onSuccess]로 이전 화면에 되돌린다.
     * 중복 등 실패 사유는 서버가 문구로 내려주므로 그대로 [NicknameEditUiState.errorMessage]에 담는다.
     */
    fun updateNickname(nickname: String, onSuccess: () -> Unit) {
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            userRepository.updateProfile(nickname = nickname)
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false) }
                    onSuccess()
                }
                .onError { code, message ->
                    // 토큰 만료 등 인증 오류는 세션 만료 흐름(→ 랜딩)이 처리하므로 필드 에러로 노출하지 않는다.
                    // 닉네임 중복 같은 실제 비즈니스 오류만 서버 문구 그대로 보여준다.
                    val display = if (ApiErrorCode.isTokenInvalid(code)) null else message.ifBlank { DEFAULT_ERROR }
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = display) }
                }
        }
    }

    /** 사용자가 입력을 다시 고치면 이전 실패 안내를 지운다. */
    fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private companion object {
        const val DEFAULT_ERROR = "닉네임 변경에 실패했어요. 잠시 후 다시 시도해주세요."
    }
}

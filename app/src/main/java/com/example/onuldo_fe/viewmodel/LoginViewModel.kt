package com.example.onuldo_fe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.utils.Validators
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val isLoginEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && !isLoading
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    /**
     * 로그인 시도. 성공 시 [onSuccess] 호출.
     * TODO: 실제 인증 API(repository) 연동. 현재는 백엔드 미연동으로 임시 처리.
     * - 이메일 형식이 올바르지 않으면 설계서 정책대로 통합 에러 메시지 노출(이메일/비번 구분 안 함).
     */
    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (!Validators.isValidEmail(state.email)) {
            _uiState.update {
                it.copy(errorMessage = "이메일 또는 비밀번호가 일치하지 않습니다")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(600) // 네트워크 지연 시뮬레이션
            // TODO: 서버 인증 결과에 따라 성공/실패 분기. 지금은 형식 유효 시 성공 처리.
            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}

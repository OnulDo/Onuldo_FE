package com.example.onuldo_fe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.repository.auth.AuthRepository
import com.example.onuldo_fe.repository.auth.AuthRepositoryProvider
import com.example.onuldo_fe.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 회원가입(계정 만들기 1/4) 상태. 실시간 유효성 결과를 함께 노출한다.
 * 세부 규칙/문구는 화면설계서(회원가입) 기준.
 */
data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isCheckingEmail: Boolean = false,
    val emailExists: Boolean = false,
    val emailCheckError: String? = null,
) {
    private val emailValid get() = Validators.isValidEmail(email)
    // 비밀번호는 이메일 포함 여부까지 검사(설계서 규칙). 닉네임은 회원가입 폼에 없어 미전달.
    private val passwordErrorMsg get() = Validators.passwordErrorMessage(password, email)
    private val passwordValid get() = password.isNotEmpty() && passwordErrorMsg == null
    private val confirmMatched get() = passwordConfirm.isNotEmpty() && passwordConfirm == password

    // 이메일: 형식이 맞으면 성공(초록), 입력이 있는데 형식이 틀리면 에러(빨강).
    // 서버에 이메일 중복확인 API가 없어 여기서는 형식만 검사한다. 중복 여부는 회원가입 호출 시점에
    // `DUPLICATE_EMAIL`로 판별된다(설계서의 실시간 중복 안내는 API 추가 전까지 구현 불가).
    val emailSuccess get() = email.isNotEmpty() && emailValid && !emailExists && emailCheckError == null
    val emailError get() = (email.isNotEmpty() && !emailValid) || emailExists || emailCheckError != null
    val emailSupport: String?
        get() = when {
            email.isEmpty() -> null
            emailExists -> "이미 가입된 이메일입니다."
            emailCheckError != null -> emailCheckError
            emailValid -> "사용 가능한 이메일입니다"
            else -> "올바른 이메일 형식이 아닙니다"
        }

    val passwordError get() = passwordErrorMsg != null
    val passwordSupport: String? get() = passwordErrorMsg

    val confirmError get() = passwordConfirm.isNotEmpty() && passwordConfirm != password
    val confirmSupport: String?
        get() = if (confirmError) "비밀번호가 일치하지 않습니다" else null

    val isContinueEnabled: Boolean
        // 약관 동의는 다음 단계(TermsAgreementScreen)에서 받으므로 여기서는 입력값만 본다.
        get() = emailValid && passwordValid && confirmMatched && !isCheckingEmail
}

class SignupViewModel(
    private val authRepository: AuthRepository = AuthRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update {
        it.copy(email = value, emailExists = false, emailCheckError = null)
    }

    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }

    fun onPasswordConfirmChange(value: String) = _uiState.update { it.copy(passwordConfirm = value) }


    /**
     * 다음 단계(약관 동의)로 진행.
     *
     * 서버 회원가입 API는 닉네임·약관까지 한 번에 받으므로 **여기서는 호출하지 않고**
     * 입력값을 [OnboardingDraft]에 넘겨 둔다. 실제 가입은 프로필 설정 완료 시점에 이뤄진다.
     */
    fun submit(onNext: () -> Unit) {
        val state = _uiState.value
        if (!state.isContinueEnabled) return

        _uiState.update { it.copy(isCheckingEmail = true, emailCheckError = null) }
        viewModelScope.launch {
            authRepository.emailExists(state.email)
                .onSuccess { exists ->
                    _uiState.update { it.copy(isCheckingEmail = false, emailExists = exists) }
                    if (!exists) {
                        OnboardingDraft.saveCredentials(
                            email = state.email,
                            password = state.password,
                            agreedRequiredTerms = false,
                        )
                        onNext()
                    }
                }
                .onError { _, message ->
                    _uiState.update {
                        it.copy(isCheckingEmail = false, emailCheckError = message)
                    }
                }
        }
    }
}

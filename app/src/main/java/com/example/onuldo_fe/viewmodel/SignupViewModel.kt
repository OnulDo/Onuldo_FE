package com.example.onuldo_fe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.onuldo_fe.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 회원가입(계정 만들기 1/4) 상태. 실시간 유효성 결과를 함께 노출한다.
 * 세부 규칙/문구는 화면설계서(회원가입) 기준.
 */
data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val agreeAll: Boolean = false,
) {
    private val emailValid get() = Validators.isValidEmail(email)
    // 비밀번호는 이메일 포함 여부까지 검사(설계서 규칙). 닉네임은 회원가입 폼에 없어 미전달.
    private val passwordErrorMsg get() = Validators.passwordErrorMessage(password, email)
    private val passwordValid get() = password.isNotEmpty() && passwordErrorMsg == null
    private val confirmMatched get() = passwordConfirm.isNotEmpty() && passwordConfirm == password

    // 이메일: 형식이 맞으면 성공(초록), 입력이 있는데 형식이 틀리면 에러(빨강).
    // 서버에 이메일 중복확인 API가 없어 여기서는 형식만 검사한다. 중복 여부는 회원가입 호출 시점에
    // `DUPLICATE_EMAIL`로 판별된다(설계서의 실시간 중복 안내는 API 추가 전까지 구현 불가).
    val emailSuccess get() = email.isNotEmpty() && emailValid
    val emailError get() = email.isNotEmpty() && !emailValid
    val emailSupport: String?
        get() = when {
            email.isEmpty() -> null
            emailValid -> "사용 가능한 이메일입니다"
            else -> "올바른 이메일 형식이 아닙니다"
        }

    val passwordError get() = passwordErrorMsg != null
    val passwordSupport: String? get() = passwordErrorMsg

    val confirmError get() = passwordConfirm.isNotEmpty() && passwordConfirm != password
    val confirmSupport: String?
        get() = if (confirmError) "비밀번호가 일치하지 않습니다" else null

    val isContinueEnabled: Boolean
        get() = emailValid && passwordValid && confirmMatched && agreeAll
}

class SignupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }

    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }

    fun onPasswordConfirmChange(value: String) = _uiState.update { it.copy(passwordConfirm = value) }

    fun toggleAgreeAll() = _uiState.update { it.copy(agreeAll = !it.agreeAll) }

    /**
     * 다음 단계(프로필 설정)로 진행.
     *
     * 서버 회원가입 API는 닉네임까지 한 번에 받으므로 **여기서는 호출하지 않고**
     * 입력값을 [OnboardingDraft]에 넘겨 둔다. 실제 가입은 프로필 설정 완료 시점에 이뤄진다.
     */
    fun submit(onNext: () -> Unit) {
        val state = _uiState.value
        if (!state.isContinueEnabled) return

        OnboardingDraft.saveCredentials(
            email = state.email,
            password = state.password,
            agreedRequiredTerms = state.agreeAll,
        )
        onNext()
    }
}

package com.example.onuldo_fe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.auth.dto.SocialProvider
import com.example.onuldo_fe.data.network.ApiErrorCode
import com.example.onuldo_fe.data.social.SocialAuthClient
import com.example.onuldo_fe.data.social.SocialAuthResult
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

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    /**
     * 이메일 로그인 오류. 화면이 **비밀번호 입력칸 아래 헬퍼 텍스트**로 노출한다.
     *
     * 소셜 로그인 오류를 여기 담으면 안 된다 — 카카오 SDK 실패가 비밀번호가 틀린 것처럼 보인다.
     * 그쪽은 [socialErrorMessage]를 쓴다.
     */
    val errorMessage: String? = null,
    /** 소셜 로그인 오류. 입력칸과 무관하므로 소셜 버튼 아래 배너로 노출한다. */
    val socialErrorMessage: String? = null,
    /** 소셜 로그인 진행 중인 제공자. 버튼 중복 탭을 막고 로딩 표시에 쓴다. */
    val socialInProgress: SocialProvider? = null,
    /**
     * 다음 소셜 로그인에서 **계정 선택 화면을 강제로 띄울지.**
     *
     * 소셜 가입이 "이미 가입된 계정"으로 막혀 이 화면으로 되돌아온 뒤에 세운다. 그대로 두면
     * SDK가 직전에 쓴 계정으로 조용히 다시 로그인해 버려서 다른 계정으로 갈아탈 방법이 없다.
     */
    val forceSocialAccountSelection: Boolean = false,
) {
    val isBusy: Boolean get() = isLoading || socialInProgress != null

    // 소셜 로그인이 진행 중일 때도 비활성화한다. 두 인증이 동시에 끝나면
    // 어느 쪽 토큰이 최종으로 남는지 불확정해지기 때문이다.
    val isLoginEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && !isBusy
}

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // 입력을 고치기 시작하면 이전 안내는 지운다. 소셜 안내도 함께 지우는 이유는,
    // "이메일로 로그인하거나…" 안내를 따라 입력을 시작한 상황이기 때문이다.
    // 다음 소셜 로그인에서 계정 선택을 강제하는 표시([forceSocialAccountSelection])는 남긴다.
    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null, socialErrorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null, socialErrorMessage = null) }
    }

    /**
     * 로그인 시도. 성공 시 토큰이 저장되고 [onSuccess]가 호출된다.
     *
     * 형식이 틀린 이메일은 서버까지 가지 않고 바로 막는다(설계서 정책상 이메일/비밀번호를 구분해
     * 알려주지 않으므로 통합 문구를 쓴다). 그 외 실패 문구는 **서버가 준 메시지를 그대로** 쓴다 —
     * 5회 실패 잠금(`LOGIN_LOCKED`) 안내도 서버 문구로 자연스럽게 노출된다.
     */
    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isBusy) return

        if (!Validators.isValidEmail(state.email)) {
            _uiState.update { it.copy(errorMessage = INVALID_CREDENTIAL_MESSAGE) }
            return
        }

        // 로딩 표시는 launch 밖에서 동기적으로 세운다. launch 안에서 세우면 코루틴이 실행되기
        // 전에 버튼을 다시 눌러 요청이 두 번 나갈 수 있다.
        _uiState.update { it.copy(isLoading = true, errorMessage = null, socialErrorMessage = null) }

        viewModelScope.launch {
            authRepository.login(state.email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onError { code, message ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // 자격 증명 오류는 설계서 문구로 통일, 나머지는 서버 안내를 그대로 노출.
                            errorMessage = if (code == ApiErrorCode.INVALID_LOGIN) {
                                INVALID_CREDENTIAL_MESSAGE
                            } else {
                                message
                            },
                        )
                    }
                }
        }
    }

    /**
     * 소셜 로그인.
     *
     * 1. SDK로 소셜 accessToken을 받는다.
     * 2. 서버 `oauth/login`에 넘긴다.
     * 3. 기존 회원이면 토큰이 발급돼 [onLoggedIn] — 바로 메인으로.
     *    신규 회원이면 토큰 없이 `isNewUser=true`만 오므로 [onNeedSignup] — 약관·닉네임 입력 후
     *    `oauth/signup`을 호출해야 한다.
     *
     * 소셜 토큰은 가입 단계까지 들고 가야 해서 [OnboardingDraft]에 보관한다.
     */
    fun loginWithSocial(
        context: Context,
        provider: SocialProvider,
        onLoggedIn: () -> Unit,
        onNeedSignup: () -> Unit,
    ) {
        if (_uiState.value.isBusy) return

        // 로그인 시작 표시도 launch 밖에서 세워 중복 실행을 막는다.
        _uiState.update {
            it.copy(socialInProgress = provider, errorMessage = null, socialErrorMessage = null)
        }

        // 직전 시도가 기존 계정과 충돌했다면 이번에는 계정을 다시 고르게 한다.
        val forceAccountSelection = _uiState.value.forceSocialAccountSelection

        viewModelScope.launch {
            when (val social = SocialAuthClient.login(context, provider, forceAccountSelection)) {
                is SocialAuthResult.Cancelled ->
                    _uiState.update { it.copy(socialInProgress = null) }

                is SocialAuthResult.Failure ->
                    _uiState.update {
                        it.copy(socialInProgress = null, socialErrorMessage = social.message)
                    }

                is SocialAuthResult.Success -> {
                    authRepository.oauthLogin(provider, social.accessToken)
                        .onSuccess { outcome ->
                            // 쓸 수 있는 계정을 받았으므로 강제 계정 선택 표시를 내린다.
                            // (취소·실패 시에는 남겨 둬야 다시 눌렀을 때도 선택 화면이 뜬다.)
                            _uiState.update {
                                it.copy(socialInProgress = null, forceSocialAccountSelection = false)
                            }
                            if (outcome.loggedIn) {
                                onLoggedIn()
                            } else {
                                // 신규 회원 — 가입 화면에서 쓸 소셜 토큰을 보관한다.
                                OnboardingDraft.saveSocial(provider, social.accessToken)
                                onNeedSignup()
                            }
                        }
                        .onError { _, message ->
                            _uiState.update {
                                it.copy(socialInProgress = null, socialErrorMessage = message)
                            }
                        }
                }
            }
        }
    }

    /**
     * 소셜 가입이 **기존 계정과 충돌해** 되돌아왔음을 알린다.
     *
     * 안내 문구를 배너에 띄우고, 다음 소셜 로그인이 계정 선택 화면을 거치도록 표시해 둔다.
     * 이 표시가 없으면 SDK가 직전 계정으로 조용히 재로그인해 같은 실패가 반복된다.
     */
    fun showExistingAccountNotice(message: String) {
        if (message.isBlank()) return
        _uiState.update {
            it.copy(socialErrorMessage = message, forceSocialAccountSelection = true)
        }
    }

    private companion object {
        const val INVALID_CREDENTIAL_MESSAGE = "이메일 또는 비밀번호가 일치하지 않습니다"
    }
}

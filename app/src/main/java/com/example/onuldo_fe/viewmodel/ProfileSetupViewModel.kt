package com.example.onuldo_fe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.ApiErrorCode
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.repository.auth.AuthRepository
import com.example.onuldo_fe.repository.auth.AuthRepositoryProvider
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import com.example.onuldo_fe.utils.ProfileAsset
import com.example.onuldo_fe.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 프로필 설정(온보딩 마지막 단계) 상태.
 * 캐릭터 실제 drawable 목록은 UI 레이어(ProfileSetupScreen)가 인덱스로 매핑한다.
 */
data class ProfileSetupUiState(
    val nickname: String = "",
    // null = 아직 캐릭터 미선택(디자인: 점선 빈 원 + "+"). 선택 시 인덱스 지정.
    val selectedCharacterIndex: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** 이메일이 중복돼 회원가입 화면으로 되돌아가야 하는 상황인지. (이메일 가입 전용) */
    val requiresEmailChange: Boolean = false,
    /**
     * 소셜 계정의 이메일로 **이미 이메일 가입이 되어 있어** 소셜 가입이 불가능한 상황인지.
     * 이 화면에서는 풀 수 없으므로 로그인 화면으로 되돌린다.
     */
    val requiresLoginRedirect: Boolean = false,
) {
    private val nicknameErrorMsg get() = Validators.nicknameErrorMessage(nickname)
    private val nicknameValid get() = nickname.isNotEmpty() && nicknameErrorMsg == null

    val nicknameError get() = nicknameErrorMsg != null
    val nicknameSupport: String? get() = nicknameErrorMsg

    // 설계서(ONBD_PROFILE_001): 캐릭터 선택 + 닉네임 유효 모두 충족 시 "계속" 활성화.
    val isContinueEnabled: Boolean
        get() = nicknameValid && selectedCharacterIndex != null && !isLoading
}

/**
 * 프로필 설정 화면. **실제 회원가입 API 호출이 일어나는 지점**이다.
 *
 * 서버가 이메일·비밀번호·닉네임·약관을 한 번에 받기 때문에, 회원가입 화면에서 모아둔
 * [OnboardingDraft] 값과 이 화면의 닉네임·캐릭터를 합쳐 `POST /api/auth/signup`을 호출한다.
 * 가입에 성공하면 환영 보너스도 이어서 지급 요청한다(서버 자동 지급이 아님).
 */
class ProfileSetupViewModel(
    private val authRepository: AuthRepository = AuthRepositoryProvider.provide(),
    private val userRepository: UserRepository = UserRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    fun onNicknameChange(value: String) =
        _uiState.update { it.copy(nickname = value, errorMessage = null) }

    fun onCharacterSelect(index: Int) =
        _uiState.update { it.copy(selectedCharacterIndex = index, errorMessage = null) }

    /**
     * 회원가입을 실행하고 성공 시 [onDone]을 호출한다.
     *
     * 닉네임 규칙 위반(`INVALID_NICKNAME` 등)은 서버 문구를 닉네임 입력칸 아래가 아니라
     * 화면 에러로 노출한다. 이메일 중복(`DUPLICATE_EMAIL`)은 이 화면에서 고칠 수 없는 값이라
     * [ProfileSetupUiState.requiresEmailChange]를 세워 회원가입 화면으로 되돌릴 수 있게 한다.
     *
     * **소셜 가입에서 이메일이 중복된 경우는 되돌릴 회원가입 화면이 없다.** 이메일을 소셜 제공자가
     * 정해 주기 때문에 사용자가 바꿀 수 없고, 소셜 로그인을 다시 눌러도 서버는 여전히 신규 회원으로
     * 판정해 같은 자리로 돌아온다(무한 반복). 그래서 [onExistingAccount]로 로그인 화면에 돌려보내
     * **이미 있는 이메일 계정으로 로그인**하도록 안내한다.
     */
    fun submit(
        onDone: () -> Unit,
        onEmailChangeRequired: (String) -> Unit = {},
        onExistingAccount: (String) -> Unit = {},
    ) {
        val state = _uiState.value
        if (!state.isContinueEnabled) return

        val socialProvider = OnboardingDraft.socialProvider
        val isSocialSignup = socialProvider != null

        // 소셜 가입은 이메일/비밀번호 대신 소셜 토큰을 쓴다. 어느 쪽이든 필요한 값이 있어야 진행한다.
        val hasRequiredDraft =
            if (isSocialSignup) OnboardingDraft.isSocialComplete else OnboardingDraft.isComplete

        if (!hasRequiredDraft) {
            // 회원가입 화면을 거치지 않고 진입한 비정상 경로.
            _uiState.update {
                it.copy(
                    errorMessage = "회원가입 정보가 없어요. 처음부터 다시 진행해주세요.",
                    requiresEmailChange = true,
                )
            }
            return
        }

        val characterIndex = state.selectedCharacterIndex ?: return
        val profileImageUrl = ProfileAsset.fromCharacterIndex(characterIndex)

        // 로딩 표시는 launch 밖에서 동기적으로 세운다. launch 안에서 세우면 코루틴이 실행되기 전에
        // "계속"을 다시 눌러 회원가입이 두 번 요청될 수 있다(가입은 비멱등 쓰기).
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val signupResult = if (socialProvider != null) {
                authRepository.oauthSignup(
                    provider = socialProvider,
                    socialAccessToken = OnboardingDraft.socialAccessToken,
                    nickname = state.nickname,
                    profileImageUrl = profileImageUrl,
                    termAgreements = OnboardingDraft.toTermAgreements(),
                )
            } else {
                authRepository.signup(
                    email = OnboardingDraft.email,
                    password = OnboardingDraft.password,
                    nickname = state.nickname,
                    profileImageUrl = profileImageUrl,
                    termAgreements = OnboardingDraft.toTermAgreements(),
                )
            }

            signupResult
                .onSuccess {
                    // 환영 보너스는 서버가 자동 지급하지 않으므로 가입 직후 요청한다.
                    // 실패하더라도(이미 지급 등) 온보딩 자체는 계속 진행한다.
                    userRepository.grantSignupBonus()

                    OnboardingDraft.clear()
                    _uiState.update { it.copy(isLoading = false) }
                    onDone()
                }
                .onError { code, message ->
                    val duplicateEmail = code == ApiErrorCode.DUPLICATE_EMAIL

                    if (duplicateEmail && isSocialSignup) {
                        // 소셜 계정의 이메일로 이미 이메일 가입이 되어 있다. 여기서도, 소셜 로그인을
                        // 다시 눌러서도 풀 수 없으므로(서버가 기존 계정에 소셜을 연동해 주기 전까지는
                        // 계속 신규 회원으로 판정된다) 남은 소셜 토큰을 버리고 로그인 화면으로 보낸다.
                        // 지우지 않으면 다음 시도에서 죽은 토큰으로 가입을 또 호출한다.
                        OnboardingDraft.clear()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = message.ifBlank { EXISTING_ACCOUNT_MESSAGE },
                                requiresLoginRedirect = true,
                            )
                        }
                        onExistingAccount(
                            "${message.ifBlank { EXISTING_ACCOUNT_MESSAGE }}\n$EXISTING_ACCOUNT_GUIDE"
                        )
                        return@onError
                    }

                    if (duplicateEmail) onEmailChangeRequired(message)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = message,
                            requiresEmailChange = duplicateEmail,
                        )
                    }
                }
        }
    }

    /** 에러 안내를 확인한 뒤 호출해 상태를 되돌린다. */
    fun consumeError() =
        _uiState.update {
            it.copy(errorMessage = null, requiresEmailChange = false, requiresLoginRedirect = false)
        }

    private companion object {
        /** 서버가 문구를 비워 보냈을 때만 쓰는 대체 문구. */
        const val EXISTING_ACCOUNT_MESSAGE = "이미 가입된 계정이에요."

        /**
         * 되돌아간 로그인 화면에서 할 수 있는 선택지를 그대로 알려준다.
         * 소셜 버튼을 다시 누르면 계정 선택 화면이 뜬다([LoginViewModel.showExistingAccountNotice]).
         */
        const val EXISTING_ACCOUNT_GUIDE =
            "이메일로 로그인하거나, 소셜 로그인을 다시 눌러 다른 계정을 선택해주세요."
    }
}

package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.ApiErrorCode
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.model.user.MyPageSummary
import com.example.onuldo_fe.repository.auth.AuthRepository
import com.example.onuldo_fe.repository.auth.AuthRepositoryProvider
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import com.example.onuldo_fe.utils.ProfileAsset
import com.example.onuldo_fe.utils.formatPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyMainUiState(
    val summary: MyPageSummary? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false, // 탈퇴 전용
    val errorMessage: String? = null,
    /** 회원 탈퇴 실패 안내(일회성). 화면이 토스트로 노출한 뒤 [MyMainViewModel.onDeleteFailedShown]으로 비움
     */
    val deleteFailedMessage: String? = null,
) {
    val nickname: String get() = summary?.nickname.orEmpty()
    val email: String get() = summary?.email.orEmpty()
    val pointText: String get() = formatPoint(summary?.currentPoint ?: 0L)

    /**
     * 서버 `profileImageUrl`에 대응하는 캐릭터 인덱스. 프리셋이 아니거나 조회 전이면 null이라
     * 화면은 기본 아바타를 보여준다. (프로필 설정 화면과 동일한 규칙)
     */
    val characterIndex: Int? get() = ProfileAsset.toCharacterIndex(summary?.profileImageUrl)
}

/** 마이 메인. `GET /api/users/me`로 프로필·보유 포인트를 가져온다. */
class MyMainViewModel(
    private val userRepository: UserRepository = UserRepositoryProvider.provide(),
    private val authRepository: AuthRepository = AuthRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyMainUiState())
    val uiState: StateFlow<MyMainUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    /**
     * 프로필·보유 포인트 조회.
     *
     * 화면이 보일 때마다 호출된다(최초 진입 포함 — `MyMainScreen`의 `RefreshOnResume`).
     * 그래서 `init`에서 따로 조회하지 않는다. 충전은 물론 챌린지 참여 예치금 차감처럼
     * 다른 화면에서 일어난 변동도 이 시점에 반영된다.
     */
    fun load() {
        // 화면이 보일 때마다 불리므로 이전 조회가 아직 돌고 있을 수 있다.
        // 끊지 않으면 늦게 온 옛 응답이 최신 잔액을 덮는다.
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.getMyPage()
                .onSuccess { summary ->
                    _uiState.update { it.copy(isLoading = false, summary = summary) }
                }
                .onError { _, message ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
        }
    }

    /**
     * 로그아웃. 서버에 로그아웃 API가 없어 클라이언트에서 토큰을 폐기한다.
     * (리프레시 토큰은 서버에 남아 있으므로, API가 생기면 함께 무효화해야 한다.)
     */
    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        _uiState.update { MyMainUiState() }
        onLoggedOut()
    }

    /**
     * 회원 탈퇴. `DELETE /api/users/me`로 계정을 삭제한다(서버가 보관 중인 인증 사진 등도 함께 파기).
     * 성공하면 무효화된 토큰을 로컬에서도 폐기하고 [onDeleted]로 진입 화면으로 되돌린다.
     * 실패 시에는 화면에 머물며 서버 문구를 노출한다.
     */
    fun deleteAccount(onDeleted: () -> Unit) {
        if (_uiState.value.isDeleting) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDeleting = true,
                    errorMessage = null,
                )
            }

            userRepository.deleteAccount()
                .onSuccess {
                    authRepository.logout()
                    _uiState.update { MyMainUiState() }
                    onDeleted()
                }
                .onError { code, message ->
                    // 그 외(진행 중 챌린지 등)는 서버 문구를, 문구가 비면 기본 안내를 노출
                    val display =
                        if (ApiErrorCode.isTokenInvalid(code)) {
                            null
                        } else {
                            message.ifBlank { DELETE_FAILED_MESSAGE }
                        }

                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            deleteFailedMessage = display,
                        )
                    }
                }
        }
    }

    /** 탈퇴 실패 안내를 화면이 노출한 뒤 호출해 한 번만 뜨도록 비운다.
     TODO: 추후 토스트 추가 부탁
     */
    fun onDeleteFailedShown() {
        _uiState.update { it.copy(deleteFailedMessage = null) }
    }

    private companion object {
        const val DELETE_FAILED_MESSAGE = "회원 탈퇴에 실패했어요. 잠시 후 다시 시도해주세요."
    }
}
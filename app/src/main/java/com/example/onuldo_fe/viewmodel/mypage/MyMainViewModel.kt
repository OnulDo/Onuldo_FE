package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.model.user.MyPageSummary
import com.example.onuldo_fe.repository.auth.AuthRepository
import com.example.onuldo_fe.repository.auth.AuthRepositoryProvider
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import com.example.onuldo_fe.utils.formatPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyMainUiState(
    val summary: MyPageSummary? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val nickname: String get() = summary?.nickname.orEmpty()
    val email: String get() = summary?.email.orEmpty()
    val pointText: String get() = formatPoint(summary?.currentPoint ?: 0L)
}

/** 마이 메인. `GET /api/users/me`로 프로필·보유 포인트를 가져온다. */
class MyMainViewModel(
    private val userRepository: UserRepository = UserRepositoryProvider.provide(),
    private val authRepository: AuthRepository = AuthRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyMainUiState())
    val uiState: StateFlow<MyMainUiState> = _uiState.asStateFlow()

    /**
     * 프로필·보유 포인트 조회.
     *
     * 화면이 보일 때마다 호출된다(최초 진입 포함 — `MyMainScreen`의 `RefreshOnResume`).
     * 그래서 `init`에서 따로 조회하지 않는다. 충전은 물론 챌린지 참여 예치금 차감처럼
     * 다른 화면에서 일어난 변동도 이 시점에 반영된다.
     */
    fun load() {
        viewModelScope.launch {
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
}

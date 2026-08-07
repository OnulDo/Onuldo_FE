package com.example.onuldo_fe.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.repository.auth.AuthRepository
import com.example.onuldo_fe.repository.auth.AuthRepositoryProvider
import kotlinx.coroutines.launch

/**
 * 앱 시작 시 자동 로그인 여부를 판정한다.
 *
 * **Activity가 아니라 ViewModel에 두는 이유**: 화면 회전·다크모드 전환처럼 구성이 바뀌면 Activity는
 * 다시 만들어진다. 판정 결과를 Activity 필드에 두면 그때마다 값이 초기화돼
 * (1) 토큰 재발급 네트워크 요청이 다시 나가고,
 * (2) 판정이 끝날 때까지 `OnuldoApp`이 컴포지션에서 빠져 `NavHost`가 새로 만들어지면서
 *     사용자가 보던 화면 대신 시작 화면으로 되돌아간다.
 * ViewModel은 구성 변경을 넘겨 살아남으므로 판정이 한 번만 일어난다.
 *
 * 어느 화면으로 갈지는 [com.example.onuldo_fe.MainActivity]가 정한다. 이 클래스는 내비게이션
 * 경로를 알 필요가 없다.
 */
class StartupViewModel(
    private val authRepository: AuthRepository = AuthRepositoryProvider.provide(),
) : ViewModel() {

    /** 세션 복구 결과. **null이면 아직 판정 전**이며, 그동안 스플래시가 유지된다. */
    var isSessionRestored by mutableStateOf<Boolean?>(null)
        private set

    init {
        viewModelScope.launch {
            isSessionRestored = authRepository.restoreSession()
        }
    }
}

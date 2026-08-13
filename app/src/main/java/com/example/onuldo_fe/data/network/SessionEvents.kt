package com.example.onuldo_fe.data.network

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 세션 만료 알림 통로.
 *
 * 리프레시 토큰까지 만료돼 자동 재발급이 실패하면 [TokenAuthenticator]가 여기로 신호를 보낸다.
 * 최상위 내비게이션이 이를 구독해 랜딩/로그인 화면으로 돌려보낸다.
 *
 * 네트워크 스레드에서 호출되므로 `tryEmit`을 쓰고, 구독자가 없을 때 신호가 사라지지 않도록
 * replay 1을 둔다. 화면 진입 후 처리했다면 [consume]으로 비운다.
 */
object SessionEvents {

    private val _sessionExpired = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    fun notifySessionExpired() {
        _sessionExpired.tryEmit(Unit)
    }

    /** 만료 신호를 처리한 뒤 호출해 중복 처리를 막는다. */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun consume() {
        _sessionExpired.resetReplayCache()
    }
}

package com.example.onuldo_fe.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * 화면이 다시 앞으로 나올 때마다 [onResume]을 호출한다. (최초 진입 시에도 1회 호출)
 *
 * 마이·지갑처럼 **다른 화면을 다녀오는 사이 서버 값이 바뀌는** 화면에서 쓴다.
 * 이들 화면의 ViewModel은 백스택 엔트리에 스코프되어 살아 있으므로 `init`의 최초 조회가
 * 다시 불리지 않는다. 충전뿐 아니라 챌린지 참여 예치금 차감·정산 환급처럼
 * **다른 도메인에서 일어난 변동**까지 덮으려면, 변동 지점마다 신호를 보내는 대신
 * 화면이 다시 보일 때 새로 읽는 편이 확실하다.
 *
 * 최초 진입에서도 호출되므로, ViewModel이 `init`에서 이미 조회한다면 그 조회는 빼거나
 * 중복 호출을 감안해야 한다.
 */
@Composable
fun RefreshOnResume(onResume: () -> Unit) {
    // 리컴포지션으로 람다가 새로 만들어져도 옵저버를 다시 붙이지 않도록 최신 값만 갈아끼운다.
    val currentOnResume by rememberUpdatedState(onResume)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) currentOnResume()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

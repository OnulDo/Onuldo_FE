package com.example.onuldo_fe.model.home.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 기기 푸시(FCM) 탭으로 결정된 랜딩 목적지를 화면 계층으로 전달하는 전역 버스
 * === 알림 누르면 -> 요청 화면으로 이동
 * [MainActivity]가 푸시 인텐트의 extras를 [pushLandingOf]로 변환해 [post]하고,
 * 로그인 상태(MAIN)에서만 존재하는 `MainScreen`이 이를 소비([consume])해 실제 탭/화면으로 이동한다.
 * 세션 복원·NavHost 준비 순서와 무관하게 안전하도록 [SessionEvents]와 동일한 전역 object로 둔다.
 */
object NotificationLandingBus {
    private val _pending = MutableStateFlow<NotificationLanding?>(null)
    val pending: StateFlow<NotificationLanding?> = _pending

    /** 푸시 탭으로 결정된 목적지를 대기열에 올린다. 소비 전까지 마지막 값만 유지한다. */
    fun post(landing: NotificationLanding) {
        _pending.value = landing
    }

    /** 소비 완료 후 대기 값을 비운다(재이동 방지) */
    fun consume() {
        _pending.value = null
    }
}

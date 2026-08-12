package com.example.onuldo_fe.data.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 현재 로그인 사용자의 프로필 이미지 공유 상태.
 * 변경 성공 시 갱신되어 구독 화면에 즉시 반영된다.
 */
object CurrentProfileImageStore {

    private val _profileImageUrl = MutableStateFlow<String?>(null)

    /** 화면이 구독하는 최신 프로필 이미지 URL. null이면 공유값 없음(각자 조회값 사용). */
    val profileImageUrl: StateFlow<String?> = _profileImageUrl.asStateFlow()

    /** 프로필 이미지 변경(PATCH) 성공 시에만 호출한다. */
    fun update(profileImageUrl: String?) {
        _profileImageUrl.value = profileImageUrl
    }
}

package com.example.onuldo_fe.data.social

import com.example.onuldo_fe.data.auth.dto.SocialProvider

/**
 * 소셜 계정 연동 정리. 실제 구현은 [SocialAuthClient]다.
 *
 * **로그아웃과 탈퇴의 차이가 이 인터페이스의 존재 이유다.**
 * - [logout] — 기기에 남은 소셜 토큰만 지우고 **연동(동의)은 유지**한다.
 *   그래서 다시 로그인해도 동의 화면이 뜨지 않는다(정상 동작).
 * - [unlink] — 제공자 서버의 연동까지 끊는다. **회원 탈퇴에서만** 쓴다.
 *
 * 인터페이스로 분리한 이유는 [com.example.onuldo_fe.repository.auth.AuthRepositoryImpl]의
 * 단위 테스트가 JVM에서 도는데 카카오 SDK는 안드로이드 프레임워크를 필요로 해서,
 * 그대로 부르면 테스트가 깨지기 때문이다. 테스트는 [NoOp]를 쓴다.
 */
interface SocialAccountLink {

    /**
     * 로그인·가입에 성공한 경로를 기록한다.
     * 탈퇴 시 **어떤 연동을 끊어야 하는지** 판단하는 유일한 근거다
     * (서버 `GET /api/users/me`는 가입 경로를 내려주지 않는다).
     */
    fun record(provider: SocialProvider)

    /** 로그아웃. 로컬 소셜 세션만 정리하고 연동은 유지한다. */
    fun logout()

    /**
     * 회원 탈퇴 시 연동 해제.
     * 이미 서버 탈퇴가 끝난 뒤의 뒷정리라 **실패해도 예외를 던지지 않는다**(로그만 남긴다).
     */
    suspend fun unlink()

    /** 소셜 SDK가 필요 없는 단위 테스트용 기본 구현. */
    object NoOp : SocialAccountLink {
        override fun record(provider: SocialProvider) = Unit
        override fun logout() = Unit
        override suspend fun unlink() = Unit
    }
}

package com.example.onuldo_fe.data.network

/**
 * 서버 에러 코드 상수. 백엔드 `GlobalErrorStatus` enum과 1:1 대응한다.
 *
 * 서버가 `message`에 완성된 한국어 문구를 담아 주므로 **화면은 원칙적으로 그 메시지를 그대로 노출**한다.
 * 이 상수들은 문구를 만들기 위한 것이 아니라, 코드별로 **동작이 달라져야 할 때**(예: 토큰 만료 시 재발급,
 * 중복 이메일일 때 특정 입력칸에 에러 표시) 분기하기 위해 쓴다.
 */
object ApiErrorCode {
    // 공통
    const val BAD_REQUEST = "BAD_REQUEST"
    const val INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR"

    // 인증/토큰
    const val UNAUTHORIZED = "UNAUTHORIZED"
    const val INVALID_TOKEN = "INVALID_TOKEN"
    const val TOKEN_EXPIRED = "TOKEN_EXPIRED"

    // 로그인/회원가입
    const val DUPLICATE_EMAIL = "DUPLICATE_EMAIL"
    const val INVALID_LOGIN = "INVALID_LOGIN"
    const val LOGIN_LOCKED = "LOGIN_LOCKED"
    const val TERMS_REQUIRED = "TERMS_REQUIRED"
    const val USER_NOT_FOUND = "USER_NOT_FOUND"

    // 닉네임
    const val INVALID_NICKNAME = "INVALID_NICKNAME"
    const val NICKNAME_TOO_SHORT = "NICKNAME_TOO_SHORT"
    const val NICKNAME_TOO_LONG = "NICKNAME_TOO_LONG"

    // 비밀번호
    const val INVALID_PASSWORD = "INVALID_PASSWORD"
    const val PASSWORD_TOO_SHORT = "PASSWORD_TOO_SHORT"
    const val PASSWORD_TOO_LONG = "PASSWORD_TOO_LONG"

    // 포인트
    const val SIGNUP_BONUS_ALREADY_GRANTED = "SIGNUP_BONUS_ALREADY_GRANTED"

    // 약관
    const val TERM_NOT_FOUND = "TERM_NOT_FOUND"
    const val INVALID_TERM_TYPE = "INVALID_TERM_TYPE"

    // 소셜 로그인
    const val INVALID_SOCIAL_TOKEN = "INVALID_SOCIAL_TOKEN"
    const val OAUTH_PROVIDER_ERROR = "OAUTH_PROVIDER_ERROR"

    /** 서버 응답을 파싱하지 못했거나 코드가 비어 있을 때 사용하는 클라이언트 전용 값. */
    const val UNKNOWN = "UNKNOWN"

    /** 토큰이 만료·무효라 재발급이 필요한 코드인지. */
    fun isTokenInvalid(code: String?): Boolean =
        code == TOKEN_EXPIRED || code == INVALID_TOKEN || code == UNAUTHORIZED
}

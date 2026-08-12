package com.example.onuldo_fe.data.auth.dto

/** 약관 종류. 서버 `TermType` enum과 이름이 일치해야 한다(Gson이 이름 그대로 직렬화). */
enum class TermType {
    SERVICE,
    PRIVACY,
    REFUND,
    AGE_14,
    MARKETING;

    companion object {
        /**
         * 회원가입 시 반드시 `true`여야 하는 약관.
         * 가입 화면에 노출되는 약관은 모두 필수로 동의받는다.
         */
        val REQUIRED = listOf(SERVICE, PRIVACY, REFUND, AGE_14)
    }
}

/**
 * 소셜 로그인 제공자. 서버 `SocialProvider` enum과 이름이 일치해야 한다.
 *
 * 서버에는 `NAVER`도 있지만 앱은 네이버 로그인을 제공하지 않으므로 여기서는 뺐다(2026-08-11).
 * 이 값은 앱이 서버로 **보내기만** 하고 응답으로 받지는 않으므로(`OAuthLoginResponse`에
 * provider 필드가 없다) 값이 빠져 있어도 역직렬화가 깨지지 않는다.
 */
enum class SocialProvider {
    EMAIL,
    KAKAO,
}

data class TermAgreementRequest(
    val termType: TermType,
    val value: Boolean,
)

data class DeviceRequest(
    val deviceId: String,
    val fcmToken: String,
)

data class EmailLoginRequest(
    val email: String,
    val password: String,
    val device: DeviceRequest,
)

/**
 * 이메일 회원가입 요청.
 *
 * 서버는 이메일·비밀번호·닉네임·약관동의를 **한 번에** 받는다. 그래서 앱의 회원가입 화면과
 * 프로필 설정 화면 입력을 모두 모은 뒤 마지막에 한 번만 호출한다.
 *
 * [profileImageUrl]은 프리셋 9종 URL(.../profile/1~9.png) 중 하나여야 하며 **필수**다.
 */
data class EmailSignupRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val profileImageUrl: String,
    val termAgreements: List<TermAgreementRequest>,
    val device: DeviceRequest,
)

data class OAuthLoginRequest(
    val provider: SocialProvider,
    val socialAccessToken: String,
    val device: DeviceRequest,
)

data class OAuthSignupRequest(
    val provider: SocialProvider,
    val socialAccessToken: String,
    val nickname: String,
    val profileImageUrl: String,
    val termAgreements: List<TermAgreementRequest>,
    val device: DeviceRequest,
)

/** 소셜 로그인 응답. 신규 사용자면 토큰이 비어 있고 [isNewUser]가 true라 가입 절차로 보내야 한다. */
data class OAuthLoginResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val isNewUser: Boolean = false,
)

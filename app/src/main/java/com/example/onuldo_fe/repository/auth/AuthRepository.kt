package com.example.onuldo_fe.repository.auth

import com.example.onuldo_fe.data.auth.dto.SocialProvider
import com.example.onuldo_fe.data.auth.dto.TermAgreementRequest
import com.example.onuldo_fe.data.network.ApiResult

/** 소셜 로그인 결과. 신규 사용자면 가입 절차로 이어가야 한다. */
data class OAuthLoginOutcome(
    val isNewUser: Boolean,
    val loggedIn: Boolean,
)

/**
 * 인증 저장소.
 *
 * 로그인·회원가입에 성공하면 구현체가 **토큰 저장소에 토큰을 반영**하므로,
 * 호출부(ViewModel)는 토큰을 직접 다루지 않는다.
 */
interface AuthRepository {

    suspend fun login(email: String, password: String): ApiResult<Unit>

    /**
     * 이메일 회원가입. 서버가 이메일·비밀번호·닉네임·약관을 한 번에 받으므로
     * 회원가입 화면과 프로필 설정 화면 입력을 모두 모아 호출한다.
     *
     * [profileImageUrl]은 `"default_asset:{n}"` 형식. null이면 서버가 랜덤 배정한다.
     */
    suspend fun signup(
        email: String,
        password: String,
        nickname: String,
        profileImageUrl: String?,
        termAgreements: List<TermAgreementRequest>,
    ): ApiResult<Unit>

    suspend fun oauthLogin(
        provider: SocialProvider,
        socialAccessToken: String,
    ): ApiResult<OAuthLoginOutcome>

    suspend fun oauthSignup(
        provider: SocialProvider,
        socialAccessToken: String,
        nickname: String,
        profileImageUrl: String?,
        termAgreements: List<TermAgreementRequest>,
    ): ApiResult<Unit>

    /**
     * 로그아웃. 서버에 로그아웃 API가 없어 **클라이언트에서 토큰을 폐기**하는 것으로 처리한다.
     * 리프레시 토큰이 서버에서 무효화되지는 않는다(서버 API 추가 시 보완 필요).
     */
    fun logout()

    val isLoggedIn: Boolean
}

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

    suspend fun emailExists(email: String): ApiResult<Boolean>

    suspend fun login(email: String, password: String): ApiResult<Unit>

    /**
     * 이메일 회원가입. 서버가 이메일·비밀번호·닉네임·약관을 한 번에 받으므로
     * 회원가입 화면과 프로필 설정 화면 입력을 모두 모아 호출한다.
     *
     * [profileImageUrl]은 프리셋 9종 URL 중 하나. 필수값이다.
     */
    suspend fun signup(
        email: String,
        password: String,
        nickname: String,
        profileImageUrl: String,
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
        profileImageUrl: String,
        termAgreements: List<TermAgreementRequest>,
    ): ApiResult<Unit>

    /**
     * 로그아웃. 서버에 로그아웃 API가 없어 **클라이언트에서 토큰을 폐기**하는 것으로 처리한다.
     * 리프레시 토큰이 서버에서 무효화되지는 않는다(서버 API 추가 시 보완 필요).
     *
     * 소셜 계정의 **연동(동의)은 유지**하고 로컬 세션만 정리한다. 연동까지 끊는 것은
     * 회원 탈퇴([unlinkSocialAccount])뿐이다.
     */
    fun logout()

    /**
     * 소셜 연동 해제. **회원 탈퇴 시에만** 호출한다 — 서버 탈퇴가 성공한 뒤 [logout]보다 먼저 부른다.
     *
     * 연동을 남겨 두면 탈퇴 후에도 카카오의 "연결된 서비스"에 오늘두가 계속 남는다.
     * 이미 계정이 사라진 뒤의 뒷정리라 실패해도 예외를 던지지 않는다.
     */
    suspend fun unlinkSocialAccount()

    /**
     * 앱 시작 시 저장된 세션을 되살린다(**자동 로그인**). 결과가 `true`면 홈, `false`면 랜딩으로 간다.
     *
     * 저장된 액세스 토큰은 수명이 30분이라 재시작 시점에는 대개 만료돼 있다. 그래서 홈으로 보내기 전에
     * 리프레시 토큰으로 **미리 재발급**해 둔다. 이렇게 하지 않으면 홈 진입 직후 첫 API가 401을 받고
     * 랜딩으로 튕겨, 화면이 한 번 깜빡인다.
     *
     * 판정:
     * - 리프레시 토큰 없음 → `false` (로그인한 적 없음)
     * - 재발급 성공 → `true`
     * - 서버가 재발급을 **거부**(401·403·무효 토큰 코드) → 토큰을 비우고 `false`
     * - 통신 실패·서버 장애(5xx)·타임아웃 → **토큰을 지우지 않는다.** 서버가 거부한 게 아니라
     *   리프레시 토큰은 아직 쓸 수 있으므로 보존하고, 남아 있는 액세스 토큰이 아직 유효하면 `true`,
     *   이미 만료됐으면 `false`를 돌려준다. 이후 요청의 401은 [TokenAuthenticator]가 다시 처리한다.
     *
     * **코루틴 취소를 제외하면 예외를 던지지 않는다.** 호출부(스플래시)가 결과를 받아야 화면이 그려지므로,
     * 예외가 새어 나가면 스플래시가 영원히 유지돼 앱이 멈춘 것처럼 보인다. 예상 못 한 오류는 `false`다.
     */
    suspend fun restoreSession(): Boolean

    val isLoggedIn: Boolean
}

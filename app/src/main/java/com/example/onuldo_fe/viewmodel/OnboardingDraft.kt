package com.example.onuldo_fe.viewmodel

import com.example.onuldo_fe.data.auth.dto.SocialProvider
import com.example.onuldo_fe.data.auth.dto.TermAgreementRequest
import com.example.onuldo_fe.data.auth.dto.TermType

/**
 * 온보딩 진행 중 입력값 보관소.
 *
 * 서버 `POST /api/auth/signup`은 이메일·비밀번호·닉네임·약관동의를 **한 번에** 받는데,
 * 앱은 회원가입 화면(이메일·비밀번호·약관)과 프로필 설정 화면(닉네임·캐릭터)으로 나뉘어 있다.
 * 그래서 회원가입 화면 입력을 여기 모아 두었다가 프로필 설정 완료 시점에 한 번만 호출한다.
 *
 * 두 화면의 ViewModel이 각각 생성되므로 공유 지점이 필요해 object로 두었다.
 * 비밀번호를 잠시 메모리에 들고 있게 되므로 **가입 완료·이탈 시 반드시 [clear]** 한다.
 */
object OnboardingDraft {

    var email: String = ""
        private set

    var password: String = ""
        private set

    /** 전체 약관 동의 여부. 화면의 "전체 약관에 동의합니다" 체크와 대응. */
    var agreedRequiredTerms: Boolean = false
        private set

    /** 소셜 가입일 때만 채워진다. 이메일 가입이면 null. */
    var socialProvider: SocialProvider? = null
        private set

    var socialAccessToken: String = ""
        private set

    /**
     * 이메일 회원가입 경로 진입. 남아 있던 소셜 정보를 지운다.
     *
     * 지우지 않으면 소셜 신규 가입 도중 뒤로 나가 이메일로 가입할 때
     * [ProfileSetupViewModel]이 여전히 소셜 경로로 판단해 입력한 이메일·비밀번호를 버린다.
     */
    fun saveCredentials(email: String, password: String, agreedRequiredTerms: Boolean) {
        this.email = email.trim()
        this.password = password
        this.agreedRequiredTerms = agreedRequiredTerms
        socialProvider = null
        socialAccessToken = ""
    }

    /**
     * 소셜 로그인에서 신규 회원으로 판정됐을 때 호출. 남아 있던 이메일 가입 정보를 지운다.
     *
     * 소셜 가입은 이메일·비밀번호 없이 소셜 토큰으로 진행되고, 약관 동의는
     * 소셜 약관 화면에서 새로 받으므로 함께 초기화한다.
     */
    fun saveSocial(provider: SocialProvider, accessToken: String) {
        socialProvider = provider
        socialAccessToken = accessToken
        email = ""
        password = ""
        agreedRequiredTerms = false
    }

    fun setAgreedRequiredTerms(agreed: Boolean) {
        agreedRequiredTerms = agreed
    }

    fun clear() {
        email = ""
        password = ""
        agreedRequiredTerms = false
        socialProvider = null
        socialAccessToken = ""
    }

    /** 이메일 회원가입 호출에 필요한 값이 모두 있는지. */
    val isComplete: Boolean
        get() = email.isNotEmpty() && password.isNotEmpty() && agreedRequiredTerms

    /** 소셜 회원가입 호출에 필요한 값이 모두 있는지. */
    val isSocialComplete: Boolean
        get() = socialProvider != null && socialAccessToken.isNotEmpty() && agreedRequiredTerms

    /**
     * 서버에 보낼 약관 동의 목록.
     *
     * 화면에 노출되는 필수 4종(서비스·개인정보·환급·만 14세)을 동의 상태로 전송한다.
     * 마케팅 수신은 화면에 항목이 없어 **동의하지 않음(false)** 으로 보낸다.
     */
    fun toTermAgreements(): List<TermAgreementRequest> =
        TermType.REQUIRED.map { TermAgreementRequest(it, agreedRequiredTerms) } +
            TermAgreementRequest(TermType.MARKETING, false)
}

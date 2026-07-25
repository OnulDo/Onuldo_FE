package com.example.onuldo_fe.navigation

/** 앱 최상위 라우트. (탭 내부 라우트는 [BottomTab] 참고) */
object Routes {
    const val LANDING = "landing"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val PROFILE_SETUP = "profile_setup"
    const val WELCOME = "welcome"
    const val MAIN = "main"

    // 마이페이지 하위 화면 (메인 탭 위에 풀스크린으로 올라감)
    const val MYPAGE_PROFILE = "mypage_profile"
    const val MYPAGE_NICKNAME = "mypage_nickname"
    const val MYPAGE_PASSWORD = "mypage_password"
    const val MYPAGE_WALLET = "mypage_wallet"
    const val MYPAGE_CHARGE = "mypage_charge"
    const val MYPAGE_WITHDRAW = "mypage_withdraw"
    const val MYPAGE_ACCOUNT = "mypage_account"

    // 챌린지 상세 흐름: 갤러리 → 상세 → 참여 → 시작 완료
    const val CHALLENGE_DETAIL = "challenge_detail"
    const val CHALLENGE_PARTICIPATE = "challenge_participate"
    const val CHALLENGE_START_DONE = "challenge_start_done"
    // 추후: PASSWORD_RESET, 알림 설정, 약관 3종 등
}

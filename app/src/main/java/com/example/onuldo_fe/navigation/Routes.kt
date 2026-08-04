package com.example.onuldo_fe.navigation

import android.net.Uri

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
    //알림 설정 -> 알림 설정페이지 추가 (시온)
    const val MYPAGE_NOTIFICATION = "mypage_notification"

    // 챌린지 상세 흐름: 갤러리 → 상세 → 참여 → 시작 완료
    // 상세는 challengeId를 경로 인자로 받는다. (네비 등록용 패턴 + 이동용 빌더 함께 사용)
    const val CHALLENGE_DETAIL_ARG = "challengeId"
    const val CHALLENGE_DETAIL = "challenge_detail/{$CHALLENGE_DETAIL_ARG}"
    fun challengeDetail(challengeId: Long) = "challenge_detail/$challengeId"

    // 참여 화면은 challengeId(경로 인자) + 상세에서 넘겨받은 제목/한줄설명/카테고리(쿼리 인자)를 받는다.
    // 상세 화면이 이미 조회한 값을 그대로 넘겨 정보박스를 채우므로 참여 화면에서 재조회하지 않는다.
    const val CHALLENGE_PARTICIPATE_ARG = "challengeId"
    const val CHALLENGE_PARTICIPATE_ARG_TITLE = "title"
    const val CHALLENGE_PARTICIPATE_ARG_DESC = "description"
    const val CHALLENGE_PARTICIPATE_ARG_CATEGORY = "category"
    const val CHALLENGE_PARTICIPATE_ARG_TIME_START = "timeStart"
    const val CHALLENGE_PARTICIPATE_ARG_TIME_END = "timeEnd"
    const val CHALLENGE_PARTICIPATE =
        "challenge_participate/{$CHALLENGE_PARTICIPATE_ARG}" +
            "?$CHALLENGE_PARTICIPATE_ARG_TITLE={$CHALLENGE_PARTICIPATE_ARG_TITLE}" +
            "&$CHALLENGE_PARTICIPATE_ARG_DESC={$CHALLENGE_PARTICIPATE_ARG_DESC}" +
            "&$CHALLENGE_PARTICIPATE_ARG_CATEGORY={$CHALLENGE_PARTICIPATE_ARG_CATEGORY}" +
            "&$CHALLENGE_PARTICIPATE_ARG_TIME_START={$CHALLENGE_PARTICIPATE_ARG_TIME_START}" +
            "&$CHALLENGE_PARTICIPATE_ARG_TIME_END={$CHALLENGE_PARTICIPATE_ARG_TIME_END}"

    // 한글·공백이 포함될 수 있어 쿼리값은 Uri.encode로 인코딩(내비게이션이 자동 디코딩)
    fun challengeParticipate(
        challengeId: Long,
        title: String,
        description: String,
        category: String,
        timeStart: String,
        timeEnd: String
    ) = "challenge_participate/$challengeId" +
        "?$CHALLENGE_PARTICIPATE_ARG_TITLE=${Uri.encode(title)}" +
        "&$CHALLENGE_PARTICIPATE_ARG_DESC=${Uri.encode(description)}" +
        "&$CHALLENGE_PARTICIPATE_ARG_CATEGORY=${Uri.encode(category)}" +
        "&$CHALLENGE_PARTICIPATE_ARG_TIME_START=${Uri.encode(timeStart)}" +
        "&$CHALLENGE_PARTICIPATE_ARG_TIME_END=${Uri.encode(timeEnd)}"

    const val CHALLENGE_START_DONE = "challenge_start_done"
    // 추후: PASSWORD_RESET, 알림 설정, 약관 3종 등

    //카메라
    const val CAMERA = "camera"
    const val PHOTO_PREVIEW = "photo_preview"

    //검증
    const val VERIFICATION_REVIEWING = "verification_reviewing"
    const val VERIFICATION_WAITING = "verification_waiting"
    const val VERIFICATION_SUCCESS = "verification_success"
    const val VERIFICATION_FAIL = "verification_fail"


}

package com.example.onuldo_fe.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle

/**
 * 지정한 `lineHeight`가 줄 상자 높이로 그대로 반영되게 한다.
 *
 * Compose `LineHeightStyle`의 기본값은 `Trim.Both`라, **한 줄짜리 `Text`는 위아래 여유 leading이
 * 모두 잘려나간다.** 그래서 `lineHeight` 지정이 무시되고 폰트 실측 높이(Pretendard 약 1.23em)로
 * 그려지며, Figma 좌표를 기준으로 맞춘 레이아웃이 실제로는 납작해진다.
 *
 * **Figma 텍스트 박스 높이가 그 스타일의 `lineHeight`와 같을 때만** 쓴다.
 * 두 값이 다른 화면에 무심코 붙이면 오히려 어긋난다
 * (예: 프로필 설정의 닉네임은 Figma 박스가 26인데 `title1Bold.lineHeight`는 40이라 적용 대상이 아니다).
 *
 * ⚠️ `lineHeight`가 폰트 실측 높이보다 **작은** 스타일에 쓰면 글자가 눌린다.
 * 현재 `headline2Bold`(30 < 32.0) · `title2ExtraBold`(20 < 24.6) · `body2Bold`(20 < 20.9)가 여기 해당한다.
 */
fun TextStyle.figmaLineBox(): TextStyle = copy(
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    ),
)

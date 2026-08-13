package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.ui.graphics.Color

/**
 * 마이페이지 전용 로컬 색상.
 *
 * 디자인 시스템 토큰(Color.kt)에 대응이 없는 값만 남긴다.
 * 토큰과 같은 값을 여기서 다시 정의하지 말 것 — 토큰을 직접 쓴다.
 */

/**
 * 금액 화면의 "보유 N P" 보조 텍스트 회색 (#80808C).
 *
 * 포인트 충전(Figma `5154:3439`)·출금(`5154:3517`)에 **토큰이 아닌 raw hex로** 찍혀 있는 값이다.
 * 더 최신인 포인트 지갑(`5652:2948`)은 같은 역할에 `dark-brown/40`을 쓰므로,
 * 디자인이 정리되면 이 상수는 `DarkBrown40`으로 흡수될 가능성이 높다.
 */
internal val MySubText = Color(0xFF80808C)

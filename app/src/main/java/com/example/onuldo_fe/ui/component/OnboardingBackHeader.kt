package com.example.onuldo_fe.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 온보딩 상단 헤더 — 뒤로가기 화살표만 표시.
 * Figma "Ready for Dev" 온보딩(회원가입·이메일 인증·프로필 설정) 기준: 진행바 없이 좌상단 뒤로가기(20dp, 8×14).
 * (뒤로가기 아이콘은 48dp IconButton 안에 중앙 정렬되어 있어 CenterStart 배치 시 화살표가 좌측 20dp 지점에 온다.)
 */
@Composable
fun OnboardingBackHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        OnulDoBackButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
        )
    }
}

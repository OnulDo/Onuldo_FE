package com.example.onuldo_fe.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.Black
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard

/**
 * 온보딩(회원가입 등) 상단 진행 헤더 — 뒤로가기 + 진행바 + "현재/전체" 표시.
 * Figma 4단계 온보딩(1/4~4/4) 헤더 기준. (WF node 4353:3106)
 */
@Composable
fun OnboardingProgressHeader(
    currentStep: Int,
    totalSteps: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fraction = (currentStep.toFloat() / totalSteps).coerceIn(0f, 1f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OnulDoBackButton(onClick = onBack)

        Spacer(Modifier.width(12.dp))

        // 진행 트랙(연한 배경) + 채움(오렌지).
        Box(
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .background(DarkBrown10, RoundedCornerShape(3.5.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .background(Persimmon, RoundedCornerShape(3.5.dp)),
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = "$currentStep/$totalSteps",
            color = Black,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

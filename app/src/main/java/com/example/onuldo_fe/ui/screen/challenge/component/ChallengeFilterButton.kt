package com.example.onuldo_fe.ui.screen.challenge.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

//탐색 화면 필터 버튼 — 27x27 원형, 1px #FC6B2B 테두리, padding 8, 슬라이더 아이콘.
@Composable
fun ChallengeFilterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val active = selected || pressed
    val background = if (active) Persimmon else White // 기본 흰 배경 (Button.svg)
    val foreground = if (active) White else Persimmon

    Box(
        modifier = modifier
            .size(27.dp)
            .clip(CircleShape)
            .background(background) // 인스턴트 색 전환 (애니메이션 없음)
            .border(1.dp, Persimmon, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        FilterIcon(
            color = foreground,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/** 슬라이더(조절) 아이콘 — 가로줄 3개 + 손잡이 3개. 피그마 Button.svg 좌표(27 뷰포트) 그대로. */
@Composable
private fun FilterIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val s = size.width / 27f      // 27x27 뷰포트 기준 스케일
        val stroke = 1.1f * s
        val knobR = 1.65f * s
        val left = 8f * s
        val right = 19f * s

        // 가로줄 3개 (위 / 중간 / 아래)
        drawLine(color, Offset(left, 9.65f * s), Offset(right, 9.65f * s), stroke, StrokeCap.Round)
        drawLine(color, Offset(left, 13.5f * s), Offset(right, 13.5f * s), stroke, StrokeCap.Round)
        drawLine(color, Offset(left, 17.35f * s), Offset(right, 17.35f * s), stroke, StrokeCap.Round)
        // 손잡이 3개 (각 줄의 지정 위치)
        drawCircle(color, knobR, Offset(10.7516f * s, 9.65f * s))
        drawCircle(color, knobR, Offset(16.2516f * s, 13.4996f * s))
        drawCircle(color, knobR, Offset(12.4f * s, 17.3492f * s))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7)
@Composable
private fun ChallengeFilterButtonPreview() {
    OnulDo_FETheme {
        Row(
            modifier = Modifier
                .background(SourCream)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChallengeFilterButton(onClick = {})                 // 기본
            ChallengeFilterButton(onClick = {}, selected = true) // 눌림/선택
        }
    }
}

package com.example.onuldo_fe.ui.screen.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.screen.mypage.MySubText
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

/**
 * 충전/출금 v2 공용 컴포넌트. (금액 입력 박스 · 금액 칩 · 하단 CTA)
 */

/** 큰 금액 표시 박스 — 흰 배경 + Persimmon 2dp 테두리, 우측 정렬 숫자 + 단위. */
@Composable
fun AmountInputBox(
    amount: String,
    unit: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(2.dp, Persimmon, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Text(
            text = amount,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            color = BlackBrown,
        )
        Text(
            text = unit,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MySubText,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

/**
 * 금액 프리셋 칩.
 *
 * 충전은 누를 때마다 금액을 더하는 방식이라 선택 상태가 없다(Figma `5154:3439`).
 * 출금은 금액을 지정하는 방식이라 선택 상태를 표시한다(Figma `5154:3517`).
 */
@Composable
fun AmountChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Persimmon else White)
            .then(
                if (selected) Modifier
                else Modifier.border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (selected) White else BlackBrown,
        )
    }
}

/** 하단 고정 CTA. 최신 디자인에서 상단 구분선이 빠졌다. */
@Composable
fun PointCtaButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnulDoButton(
        text = text,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.padding(vertical = 12.dp),
        fontSize = 17.sp,
    )
}

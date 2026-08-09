package com.example.onuldo_fe.ui.screen.mypage.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown30
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard

/**
 * 마이페이지 공용 안내 박스 — 아이콘 + 안내 문구를 세로로 담는다.
 *
 * Persimmon 10% 배경 + Line(DarkBrown 30%) 1dp 테두리, 라운드 16dp. 높이는 내용에 맞춰 유동적이다.
 * (예: 충전 화면 "정식출시 이후 업데이트 예정이에요!")
 *
 * @param text 아이콘 아래 안내 문구. 아이콘과 [textTopGap]만큼 띄운다.
 */
@Composable
fun MyPageNoticeBox(
    @DrawableRes iconRes: Int,
    text: String,
    modifier: Modifier = Modifier,
    iconWidth: Dp = 128.dp,
    iconHeight: Dp = 106.dp,
    textTopGap: Dp = 26.dp,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Persimmon10)
            .border(1.dp, DarkBrown30, RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 20.dp),
        // 고정 높이로 쓸 때(예: 출금 화면 186dp) 내용이 세로 중앙에 오도록. wrap 높이면 영향 없음.
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(width = iconWidth, height = iconHeight),
        )
        Spacer(Modifier.height(textTopGap))
        Text(
            text = text,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = DarkBrown,
            textAlign = TextAlign.Center,
        )
    }
}

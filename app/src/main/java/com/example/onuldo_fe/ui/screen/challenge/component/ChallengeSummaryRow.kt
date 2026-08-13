package com.example.onuldo_fe.ui.screen.challenge.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Pretendard

/**
 * 요약 한 줄 (라벨 왼쪽 / 값 오른쪽).
 * 챌릱지 참여 화면 & 시작 완료 화면이 동일해서 공통으로 분리
 */
@Composable
fun ChallengeSummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //읜쪽
        Text(
            text = label,
            style = OnulDoTypography.caption2Medium,
            color = DarkBrown
        )
        //오른쪽
        Text(
            text = value,
            style = OnulDoTypography.caption2Bold,
            color = BlackBrown,
            textAlign = TextAlign.Right
        )
    }
}

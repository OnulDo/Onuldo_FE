package com.example.onuldo_fe.ui.screen.challenge.participate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

/**
 * 챌린지 참여 화면의 선택 칩.
 * 진행 기간(2주/4주/8주/12주)과 도전금(10,000P/…)이 모양이 같아 하나로 재사용.
 */
@Composable
fun OptionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    Box(
        modifier = modifier
            .height(40.dp)   // 너비는 호출부에서 weight로 지정 (Row 폭에 맞춰 늘어남)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Persimmon else White)
            .border(
                width = 1.dp,
                color = if (selected) Persimmon else DarkBrown40,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            lineHeight = 13.sp,
            color = if (selected) White else BlackBrown
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390)
@Composable
private fun ChallengeOptionChipPreview() {
    OnulDo_FETheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SourCream)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OptionChip(text = "2주", onClick = {}, modifier = Modifier.weight(1f))
            OptionChip(text = "4주", onClick = {}, selected = true, modifier = Modifier.weight(1f))
            OptionChip(text = "10,000P", onClick = {}, modifier = Modifier.weight(1f))
        }
    }
}

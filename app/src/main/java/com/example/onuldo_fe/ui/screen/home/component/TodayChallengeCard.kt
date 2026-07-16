package com.example.onuldo_fe.ui.screen.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

@Composable
fun TodayChallengeCard(
    todayChallenge: TodayChallenge,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = Persimmon10,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                border = BorderStroke(1.dp, Persimmon.copy(alpha = 0.55f)),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        Text(
            text = todayChallenge.date,
            color = Persimmon,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(34.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = todayChallenge.title,
                color = Persimmon,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = todayChallenge.progressText,
                color = Persimmon,
                fontSize = 9.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(White, RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .weight(todayChallenge.progress.coerceIn(0.001f, 1f))
                    .height(5.dp)
                    .background(Persimmon, RoundedCornerShape(50))
            )
            Spacer(
                modifier = Modifier.weight((1f - todayChallenge.progress).coerceIn(0.001f, 1f))
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 360)
@Composable
private fun TodayChallengeCardPreview() {
    OnulDo_FETheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SourCream)
                .padding(22.dp)
        ) {
            TodayChallengeCard(
                todayChallenge = TodayChallenge(
                    date = "5월 20일 (수)",
                    title = "오늘의 챌린지",
                    progressText = "1/4 완료",
                    progress = 0.25f
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

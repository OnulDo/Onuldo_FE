package com.example.onuldo_fe.ui.component.home

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.model.home.TodayChallenge
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
fun TodayChallengeCard(
    todayChallenge: TodayChallenge,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = Persimmon10,
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                border = BorderStroke(1.dp, Persimmon.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp)
            )
            .height(148.dp)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = todayChallenge.date,
            color = BlackBrown,
            fontFamily = Pretendard,
            fontSize = 20.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(42.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_today_challenge_title),
                color = BlackBrown,
                fontFamily = Pretendard,
                fontSize = 17.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Persimmon, fontWeight = FontWeight.Black)) {
                        append(todayChallenge.completedCount.toString())
                    }
                    withStyle(SpanStyle(color = BlackBrown, fontWeight = FontWeight.Medium)) {
                        append("/${todayChallenge.totalCount} 완료")
                    }
                },
                fontFamily = Pretendard,
                fontSize = 12.sp,
                lineHeight = 14.sp,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(SourCream, RoundedCornerShape(50))
                .border(
                    BorderStroke(1.dp, Persimmon.copy(alpha = 0.2f)),
                    RoundedCornerShape(50)
                )
        ) {
            Box(
                modifier = Modifier
                    .weight(todayChallenge.progress.coerceIn(0.001f, 1f))
                    .height(8.dp)
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
                    progress = 0.25f,
                    completedCount = 1,
                    totalCount = 4
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

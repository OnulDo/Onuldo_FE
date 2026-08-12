package com.example.onuldo_fe.ui.screen.home.components

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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.model.home.TodayChallenge
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Persimmon50
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TodayChallengeCard(
    todayChallenge: TodayChallenge,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    // TODO: 완료 수 조합 글자 스타일과 42dp 여백 토큰 추가 후 교체

    Column(
        modifier = modifier
            .background(
                color = Persimmon10,
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                border = BorderStroke(1.dp, Persimmon50),
                shape = RoundedCornerShape(14.dp)
            )
            .height(148.dp)
            .padding(horizontal = spacing.spacing16, vertical = spacing.spacing16)
    ) {
        Text(
            // 서버의 yyyy-MM-dd 날짜를 화면 표시 형식으로 바꾼다.
            text = todayChallenge.date.toHomeDateText(),
            color = BlackBrown,
            style = OnulDoTypography.title2ExtraBold,
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
                style = OnulDoTypography.body2Bold,
            )
            Text(
                text = buildAnnotatedString {
                    // Caption2/Bold
                    withStyle(OnulDoTypography.caption2Bold.toSpanStyle().copy(color = Persimmon)) {
                        append(todayChallenge.completedCount.toString())
                    }
                    // Caption2/Medium
                    withStyle(OnulDoTypography.caption2Medium.toSpanStyle().copy(color = BlackBrown)) {
                        append("/${todayChallenge.totalCount} 완료")
                    }
                },
                style = OnulDoTypography.caption2Medium,
            )
        }

        Spacer(modifier = Modifier.height(spacing.spacing10))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(SourCream, RoundedCornerShape(50))
                .border(
                    BorderStroke(1.dp, Persimmon20),
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

private val homeDateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)

private fun String.toHomeDateText(): String =
    runCatching { LocalDate.parse(this).format(homeDateFormatter) }
        .getOrDefault(this)

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

package com.example.onuldo_fe.ui.screen.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.model.home.HomeCompletedChallenge
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

@Composable
fun HomeCompletedChallengeCard(
    completedChallenge: HomeCompletedChallenge,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    // TODO: 10sp Regular·11sp Bold·12sp Medium 글자 스타일과 15·17·27dp 여백 토큰 추가 후 교체

    Box(
        modifier = modifier
            .height(56.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, DarkBrown40), RoundedCornerShape(14.dp))
    ) {
        Text(
            text = completedChallenge.time,
            color = DarkBrown50,
            modifier = Modifier.padding(start = 15.dp, top = spacing.spacing12),
            fontFamily = Pretendard,
            fontSize = 10.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = completedChallenge.title,
            color = BlackBrown,
            modifier = Modifier.padding(start = 15.dp, top = 31.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        CompletedChallengeResult(completedChallenge)
    }
}

@Composable
private fun BoxScope.CompletedChallengeResult(completedChallenge: HomeCompletedChallenge) {
    val spacing = LocalSpacing.current

    when (completedChallenge) {
        is HomeCompletedChallenge.Party -> Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 15.dp, end = 17.dp)
                .width(70.dp)
                .height(24.dp)
                .background(Green2, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    R.string.home_completed_party_result,
                    completedChallenge.completedMemberCount,
                    completedChallenge.totalMemberCount
                ),
                color = Green,
                fontFamily = Pretendard,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        is HomeCompletedChallenge.Personal -> {
            if (completedChallenge.streakDays <= 0) return
            Text(
                text = stringResource(
                    R.string.home_completed_personal_streak,
                    completedChallenge.streakDays
                ),
                color = Green,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = spacing.spacing20, end = 27.dp),
                fontFamily = Pretendard,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390)
@Composable
private fun HomeCompletedChallengeCardPreview() {
    OnulDo_FETheme {
        val spacing = LocalSpacing.current
        HomeCompletedChallengeCard(
            HomeCompletedChallenge.Party("06:30", "새벽 러너 파티", 3, 3),
            Modifier.fillMaxWidth().padding(horizontal = spacing.spacing20)
        )
    }
}

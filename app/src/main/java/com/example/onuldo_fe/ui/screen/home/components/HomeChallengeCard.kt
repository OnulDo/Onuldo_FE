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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.model.home.ChallengeStatus
import com.example.onuldo_fe.model.home.HomeChallenge
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown80
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon80
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeChallengeCard(
    challenge: HomeChallenge,
    modifier: Modifier = Modifier,
    onVerifyClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    val actionColors = challenge.actionColors()
    // TODO: 18sp Bold·12sp Medium·13sp Medium 글자 스타일과 2·6·14·35dp 여백 토큰 추가 후 교체

    Column(
        modifier = modifier
            .height(111.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(
                border = BorderStroke(1.dp, DarkBrown40),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = spacing.spacing12)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = challenge.title,
                    color = BlackBrown,
                    fontFamily = Pretendard,
                    fontSize = 18.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                challenge.subtitleTextOrNull()?.let { subtitle ->
                    // 0일 연속 성공이면 문구와 간격을 모두 숨긴다.
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = subtitle,
                        color = DarkBrown50,
                        fontFamily = Pretendard,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = stringResource(R.string.home_challenge_d_day, challenge.remainingDays),
                // Figma의 D-day 텍스트 규격(12sp Bold, 행간 22sp, 오른쪽 정렬)
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DarkBrown80,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.End
                )
            )
        }

        Spacer(modifier = Modifier.height(35.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = challenge.verifiedAt?.let {
                        stringResource(R.string.home_challenge_verified_at, it.toDisplayText())
                    } ?: stringResource(R.string.home_challenge_deadline, challenge.deadlineAt.toDisplayText()),
                    color = challenge.deadlineColor(),
                    fontFamily = Pretendard,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                //인증 마감 1시간 전부터 표시
                challenge.remainingMinutes?.takeIf { it in 0..60 }?.let { minutes ->
                    Spacer(modifier = Modifier.width(spacing.spacing10))
                    Box(
                        modifier = Modifier
                            .background(Persimmon10, RoundedCornerShape(10.dp))
                            .padding(horizontal = spacing.spacing10, vertical = 2.dp)
                    ) {
                        Text(
                            text = minutes.toRemainingTimeText(),
                            color = Persimmon,
                            fontFamily = Pretendard,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (challenge.status == ChallengeStatus.NeedCertification && challenge.canVerify) {
                HomeVerifyButton(
                    onClick = onVerifyClick,
                    width = 78.dp,
                    height = 26.dp,
                    iconSize = 12.dp,
                    fontSize = 10.sp,
                    lineHeight = 12.sp
                )
            } else if (challenge.status != ChallengeStatus.NeedCertification) {
                Box(
                    modifier = Modifier
                        .background(actionColors.background, RoundedCornerShape(50))
                        .width(78.dp)
                        .height(26.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(challenge.status.actionTextRes()),
                        color = actionColors.text,
                        fontFamily = Pretendard,
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private data class ChallengeActionColors(
    val background: Color,
    val text: Color
)

private fun HomeChallenge.actionColors(): ChallengeActionColors {
    return when (status) {
        ChallengeStatus.NeedCertification -> ChallengeActionColors(
            background = White,
            text = Persimmon
        )

        ChallengeStatus.WaitingReview -> ChallengeActionColors(
            background = DarkBrown10,
            text = DarkBrown
        )

        ChallengeStatus.Failed -> ChallengeActionColors(
            background = Red2,
            text = Red
        )

        ChallengeStatus.Success -> ChallengeActionColors(
            background = Green2,
            text = Green
        )
    }
}

private fun HomeChallenge.deadlineColor(): Color = when (status) {
    ChallengeStatus.NeedCertification -> Persimmon80
    ChallengeStatus.WaitingReview,
    // TODO: Red80 색상 토큰 추가 후 교체
    ChallengeStatus.Failed -> Red.copy(alpha = 0.8f)
    ChallengeStatus.Success -> Green
}

@Composable
private fun Int.toRemainingTimeText(): String {
    val hours = this / 60
    val minutes = this % 60
    return when {
        hours > 0 && minutes > 0 -> stringResource(R.string.home_challenge_hours_minutes_left, hours, minutes)
        hours > 0 -> stringResource(R.string.home_challenge_hours_left, hours)
        else -> stringResource(R.string.home_challenge_minutes_left, minutes)
    }
}

private fun ChallengeStatus.actionTextRes(): Int = when (this) {
    ChallengeStatus.NeedCertification -> R.string.home_challenge_action_verify
    ChallengeStatus.WaitingReview -> R.string.home_challenge_action_waiting_review
    ChallengeStatus.Failed -> R.string.home_challenge_action_failed
    ChallengeStatus.Success -> R.string.home_challenge_action_success
}

@Composable
private fun HomeChallenge.subtitleTextOrNull(): String? = when (status) {
    ChallengeStatus.NeedCertification,
    ChallengeStatus.Success -> streakDays.takeIf { it > 0 }
        ?.let { stringResource(R.string.home_challenge_streak, it) }
    ChallengeStatus.WaitingReview -> stringResource(R.string.home_challenge_waiting)
    ChallengeStatus.Failed -> stringResource(R.string.home_challenge_streak_broken)
}

private val homeTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

private fun LocalTime.toDisplayText(): String = format(homeTimeFormatter)

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 360)
@Composable
private fun HomeChallengeCardPreview() {
    OnulDo_FETheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SourCream)
                .padding(22.dp)
        ) {
            HomeChallengeCard(
                challenge = HomeChallenge(
                    title = "30분 러닝",
                    streakDays = 12,
                    remainingDays = 12,
                    deadlineAt = LocalTime.of(7, 0),
                    status = ChallengeStatus.NeedCertification
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

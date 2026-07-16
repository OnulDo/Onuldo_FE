package com.example.onuldo_fe.ui.screen.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.home.model.ChallengeStatus
import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown30
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

@Composable
fun HomeChallengeCard(
    challenge: HomeChallenge,
    modifier: Modifier = Modifier
) {
    val actionColors = challenge.actionColors()

    Column(
        modifier = modifier
            .background(White, RoundedCornerShape(10.dp))
            .border(
                border = BorderStroke(1.dp, DarkBrown30),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
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
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = challenge.subtitle,
                    color = DarkBrown50,
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = challenge.dDay,
                color = DarkBrown,
                fontSize = 9.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = challenge.deadline,
                color = actionColors.text,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .background(actionColors.background, RoundedCornerShape(50))
                    .border(
                        border = BorderStroke(
                            width = if (challenge.status == ChallengeStatus.NeedCertification) 1.dp else 0.dp,
                            color = if (challenge.status == ChallengeStatus.NeedCertification) Persimmon else actionColors.background
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (challenge.status == ChallengeStatus.NeedCertification) {
                        Image(
                            painter = painterResource(id = R.drawable.home_camera_icon),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = challenge.actionText,
                        color = actionColors.text,
                        fontSize = 9.sp,
                        lineHeight = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
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

        ChallengeStatus.WaitingReview,
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
                    subtitle = "12일 연속 성공",
                    dDay = "D-12",
                    deadline = "7:00 마감",
                    actionText = "인증하기",
                    status = ChallengeStatus.NeedCertification
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

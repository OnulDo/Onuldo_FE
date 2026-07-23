package com.example.onuldo_fe.ui.screen.challenge.participate
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeInfoBox
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeNoticeBox
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeSummaryRow
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown30
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Green3
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream

// 더미 데이터 — API 연동 시 교체
private val startDoneSummaryItems = listOf(
    "진행 기간" to "5/20 ~ 6/16 (28일)",
    "인증 시각" to "오전 6:00",
    "예치 도전금" to "10,000P",
    "예상 환급금" to "11,500P (성공 시)"
)

@Composable
fun StartDoneScreen(
    challenge: Challenge = Challenge(id = 0, title = "새벽 6시 기상", participantCount = 1234),
    category: String = "시간 챌린지",                    // TODO: 실제 데이터
    subtitle: String = "오늘부터 28일간 함께 갓생해요",   // TODO: 실제 데이터(기간에서 파생)
    firstVerifyTime: String = "내일 오전 5:00 ~ 7:00",   // TODO: 실제 데이터
    onHomeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(93.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Green3),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.challenge_success_icon),
                contentDescription = null,
                modifier = Modifier.size(width = 101.dp, height = 129.dp)
            )
        }

        Spacer(Modifier.height(15.dp))

        Text(
            text = "챌린지가 시작되었어요!",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 22.sp,
            color = BlackBrown,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 13.sp,
            color = DarkBrown70,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(48.dp))

        ChallengeInfoBox(
            height = 184.dp,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = challenge.title,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 17.sp,
                    letterSpacing = (-0.02).em,
                    color = BlackBrown,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = category,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                    letterSpacing = 0.04.em,
                    color = DarkBrown,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Spacer(Modifier.height(14.dp))

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = DarkBrown30
                )

                Spacer(Modifier.height(11.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    startDoneSummaryItems.forEachIndexed { index, (label, value) ->
                        if (index > 0) Spacer(Modifier.height(8.dp))
                        ChallengeSummaryRow(label = label, value = value)
                    }
                }
            }
        }

        Spacer(Modifier.height(21.dp))

        ChallengeNoticeBox(
            height = 90.dp,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.padding(start = 20.dp, top = 18.dp)) {
                Text(
                    text = "첫 인증",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    letterSpacing = 0.04.em,
                    color = Persimmon
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    text = firstVerifyTime,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    letterSpacing = (-0.01).em,
                    color = BlackBrown
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "알림으로 알려드릴게요",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                    color = DarkBrown
                )
            }
        }

        Spacer(Modifier.height(95.dp))

        OnulDoButton(
            text = "홈으로 가기",
            onClick = onHomeClick
        )

        Spacer(Modifier.height(42.dp))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun StartDoneScreenPreview() {
    OnulDo_FETheme {
        StartDoneScreen()
    }
}

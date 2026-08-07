package com.example.onuldo_fe.ui.screen.challenge.participate
import com.example.onuldo_fe.model.challenge.ParticipationResult

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
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
fun StartDoneScreen(
    result: ParticipationResult,   // 참여 API 응답 — 기간/도전금/환급금
    title: String,                 // 챌린지 제목(상세에서 전달)
    category: String,              // 카테고리 라벨(상세에서 전달)
    timeStart: String,             // 인증 시작 "HH:mm:ss"(상세에서 전달, 없으면 "")
    timeEnd: String,               // 인증 마감 "HH:mm:ss"
    onHomeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val summaryItems = listOf(
        "진행 기간" to "${formatMonthDay(result.startDate)} ~ ${formatMonthDay(result.endDate)} (${result.durationDays}일)",
        "인증 시각" to formatTimeRange(timeStart, timeEnd),
        "예치 도전금" to "%,dP".format(result.depositAmount),
        "예상 환급금" to "%,dP (성공 시)".format(result.expectedRefundAmount)
    )
    val subtitle = "오늘부터 ${result.durationDays}일간 함께 갓생해요"
    val firstVerifyTime = if (timeStart.isNotBlank() && timeEnd.isNotBlank()) {
        "내일 ${formatTimeRange(timeStart, timeEnd)}"
    } else {
        "내일부터 인증할 수 있어요"
    }
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

        Spacer(Modifier.height(spacing.spacing8))

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

        Spacer(Modifier.height(spacing.spacing48))

        ChallengeInfoBox(
            height = 184.dp,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column {
                Spacer(Modifier.height(spacing.spacing16))

                Text(
                    text = title,
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
                    summaryItems.forEachIndexed { index, (label, value) ->
                        if (index > 0) Spacer(Modifier.height(spacing.spacing8))
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

//(파싱 실패 시 원문 유지)
private fun formatMonthDay(date: String): String = runCatching {
    val parts = date.split("-")
    "${parts[1].toInt()}/${parts[2].toInt()}"
}.getOrDefault(date)

// 범위 표기: 24시간제. withAmPm=true면 시작에만 오전/오후를 붙인다(끝은 항상 시간만).
// 시각 미설정(둘 중 하나라도 빈 값)이면 종일 표기로 폴백해, 호출부에서 별도 null/빈값 처리를 하지 않아도 된다.
fun formatTimeRange(start: String, end: String, withAmPm: Boolean = true): String {
    if (start.isBlank() || end.isBlank()) return if (withAmPm) "오전 00:00 ~ 23:00" else "00:00 ~ 23:00"
    return runCatching {
        val startText = format24h(start, withAmPm = withAmPm)
        val endText = format24h(end, withAmPm = false)

        "$startText ~ $endText"
    }.getOrDefault("${start.take(5)} ~ ${end.take(5)}")
}

// "HH:mm" withAmPm이면 앞에 오전/오후 표시
private fun format24h(time: String, withAmPm: Boolean): String {
    val (h, m) = time.split(":").map { it.toInt() }
    val hhmm = "%02d:%02d".format(h, m)

    return if (withAmPm) {
        "${if (h < 12) "오전" else "오후"} $hhmm"
    } else {
        hhmm
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun StartDoneScreenPreview() {
    OnulDo_FETheme {
        StartDoneScreen(
            result = ParticipationResult(
                startDate = "2026-05-20",
                endDate = "2026-06-16",
                durationWeeks = 4,
                durationDays = 28,
                depositAmount = 10_000,
                expectedRefundAmount = 11_500
            ),
            title = "새벽 6시 기상",
            category = "시간 챌린지",
            timeStart = "06:00:00",
            timeEnd = "07:00:00"
        )
    }
}

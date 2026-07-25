package com.example.onuldo_fe.ui.screen.challenge.participate
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeInfoBox
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeNoticeBox
import com.example.onuldo_fe.ui.screen.challenge.participate.component.OptionChip
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeSummaryRow
import com.example.onuldo_fe.ui.screen.challenge.participate.component.InsufficientPointDialog
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream

// 더미 데이터 — API 연동 시 교체
private val periodOptions = listOf("2주", "4주", "8주", "12주")
private val pointOptions = listOf("10,000P", "20,000P", "30,000P", "50,000P")
private val summaryItems = listOf(
    "진행 기간" to "4주 (28일)",
    "인증 방식" to "하루 1회 자율 인증",
    "예치 도전금" to "10,000P"
)

@Composable
fun ParticipateScreen(
    challenge: Challenge = Challenge(id = 0, title = "새벽 6시 기상", participantCount = 1234),
    category: String = "생활루틴 챌린지",               // TODO: 실제 데이터
    description: String = "매일 새벽 6시까지 기상하기",   // TODO: 실제 데이터
    // TODO: 실제 보유 포인트와 선택 도전금 비교로 교체. true면 시작(완료 화면), false면 잔액 부족 팝업.
    hasEnoughPoint: Boolean = true,
    onBackClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onChargePoint: () -> Unit = {},   // 포인트 충전 화면 연결
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf<String?>(null) }
    var selectedPoint by remember { mutableStateOf<String?>(null) }
    // 잔액 부족 다이얼로그도 화면 이동이 아니라 이 화면의 상태(State)
    var showInsufficientDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            OnulDoBackButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            )
            Text(
                text = "챌린지 참여",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                color = BlackBrown,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        ChallengeInfoBox(
            height = 100.dp,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.padding(start = 16.dp, top = 20.dp)) {
                Text(
                    text = category,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    letterSpacing = 0.04.em,
                    color = DarkBrown
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = challenge.title,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    color = BlackBrown
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = description,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = DarkBrown70
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        SectionLabel(text = "진행 기간")

        Spacer(Modifier.height(7.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            periodOptions.forEach { option ->
                OptionChip(
                    text = option,
                    onClick = { selectedPeriod = option },
                    selected = selectedPeriod == option,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        NoticeBox(
            title = "성공 조건",
            lines = listOf(
                "위 챌린지는 05:30~ 06:30내에 인증을 진행해주세요",
                "일정 수준 이상 실패 시 도전금이 차감 됩니다."
            ),
            height = 100.dp,
            topPadding = 22.dp
        )

        Spacer(Modifier.height(29.dp))

        Row(
            modifier = Modifier.padding(start = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "도전금 설정",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                letterSpacing = (-0.01).em,
                color = BlackBrown
            )
            Spacer(Modifier.width(9.dp))
            Text(
                text = "1만~2만P 권장",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                color = Persimmon
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            pointOptions.forEach { option ->
                OptionChip(
                    text = option,
                    onClick = { selectedPoint = option },
                    selected = selectedPoint == option,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(26.dp))

        NoticeBox(
            title = "성공 시 100% 환급 + 보상금",
            lines = listOf("주 1회 실패 인정, 그 외 비례 차감"),
            height = 60.dp,
            topPadding = 12.dp // TODO: 내부 위 여백 미지정 — 60에 맞춘 추정값
        )

        Spacer(Modifier.height(30.dp))

        ChallengeInfoBox(
            height = 90.dp,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp)) {
                summaryItems.forEachIndexed { index, (label, value) ->
                    if (index > 0) Spacer(Modifier.height(8.dp))
                    ChallengeSummaryRow(label = label, value = value)
                }
            }
        }

        Spacer(Modifier.height(46.dp))

        // 진행 기간 + 도전금 둘 다 선택돼야 활성화
        val canStart = selectedPeriod != null && selectedPoint != null

        OnulDoButton(
            text = "도전 시작하기",
            onClick = {
                // 도전금 충분하면 시작 완료 화면으로, 부족하면 잔액 부족 팝업 노출
                if (hasEnoughPoint) onStartClick() else showInsufficientDialog = true
            },
            enabled = canStart
        )

        Spacer(Modifier.height(42.dp))
    }

    if (showInsufficientDialog) {
        InsufficientPointDialog(
            onDismiss = { showInsufficientDialog = false },
            onCharge = onChargePoint
        )
    }
}

//제목
@Composable
private fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 13.sp,
        letterSpacing = (-0.01).em,
        color = BlackBrown,
        modifier = modifier.padding(start = 24.dp)
    )
}

//안내 박스
@Composable
private fun NoticeBox(
    title: String,
    lines: List<String>,
    height: Dp,
    topPadding: Dp,
    modifier: Modifier = Modifier
) {
    ChallengeNoticeBox(
        height = height,
        modifier = modifier.padding(horizontal = 20.dp)
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = topPadding)) {
            Text(
                text = title,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                letterSpacing = (-0.01).em,
                color = Persimmon
            )
            lines.forEachIndexed { index, line ->
                Spacer(Modifier.height(if (index == 0) 7.dp else 8.dp))
                Text(
                    text = line,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    color = DarkBrown
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ParticipateScreenPreview() {
    OnulDo_FETheme {
        ParticipateScreen()
    }
}

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
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream

private val periodOptions = listOf("2주", "4주", "8주", "12주")
private val pointOptions = listOf("10,000P", "20,000P", "30,000P", "50,000P")

@Composable
fun ParticipateScreen(
    // 아래 값들의 기본값은 @Preview 전용. 실제 값은 Route가 상세에서 넘겨받아 주입한다.
    challenge: Challenge = Challenge(id = 0L, title = "새벽 6시 기상", participantCount = 1234),
    category: String = "생활루틴 챌린지",
    description: String = "매일 새벽 6시까지 기상하기",
    timeStart: String = "05:30:00",   // 인증 시작 "HH:mm:ss" (없으면 "")
    timeEnd: String = "06:30:00",     // 인증 마감 "HH:mm:ss" (없으면 "")
    // 참여 API 요청 중이면 버튼 비활성 (중복 제출 방지)
    isSubmitting: Boolean = false,
    // 포인트 부족 팝업 표시 여부(서버 INSUFFICIENT_POINT 응답 시) + 닫기 콜백
    showInsufficientDialog: Boolean = false,
    onDismissInsufficient: () -> Unit = {},
    // 지갑 요약의 보유 포인트— 포인트 부족 팝업의 "보유 포인트"에 사용
    ownedPoint: Int = 0,
    onBackClick: () -> Unit = {},
    // 선택한 기간(주)·도전금(P)을 상위(Route)로 전달 → 실제 참여 API 호출
    onStartClick: (durationWeeks: Int, depositAmount: Int) -> Unit = { _, _ -> },
    onChargePoint: () -> Unit = {},   // 포인트 충전 화면 연결
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf<String?>(null) }
    var selectedPoint by remember { mutableStateOf<String?>(null) }
    val spacing = LocalSpacing.current

    // 선택 칩의 API용 파싱을 한 곳에서만 수행 (요약/버튼/다이얼로그가 공유, 형식 변경 시 단일 지점) - 피드백
    val selectedWeeks = selectedPeriod?.removeSuffix("주")?.toIntOrNull()
    val selectedDeposit = selectedPoint?.filter { it.isDigit() }?.toIntOrNull()

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
                // 패딩 없이 정렬만 — IconButton 중앙정렬로 화살표가 가로 20에 맞음(본문과 정렬)
                modifier = Modifier.align(Alignment.CenterStart)
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

        Spacer(Modifier.height(spacing.spacing20))

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

        Spacer(Modifier.height(spacing.spacing20))

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

        // 인증 시간대는 상세에서 받은 timeStart/timeEnd로 구성(초 단위 제거). 없으면 자율 인증 문구.
        val verifyTimeLine = if (timeStart.isNotBlank() && timeEnd.isNotBlank()) {
            "위 챌린지는 ${timeStart.take(5)} ~ ${timeEnd.take(5)}내에 인증을 진행해주세요"
        } else {
            "하루 한 번 자율 인증으로 진행해주세요"
        }

        NoticeBox(
            title = "성공 조건",
            lines = listOf(
                verifyTimeLine,
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

        Spacer(Modifier.height(spacing.spacing26))

        NoticeBox(
            title = "성공 시 100% 환급 + 보상금",
            lines = listOf("주 1회 실패 인정, 그 외 비례 차감"),
            height = 60.dp,
            topPadding = 12.dp
        )

        Spacer(Modifier.height(spacing.spacing30))

        // 요약 박스 — 선택한 진행 기간/도전금이 그대로 반영된다(미선택 시 "-")
        val summaryItems = listOf(
            "진행 기간" to (selectedWeeks?.let { "${it}주 (${it * 7}일)" } ?: "-"),
            "인증 방식" to  if (timeStart.isNotBlank() && timeEnd.isNotBlank()) {
                "${timeStart.take(5)} ~ ${timeEnd.take(5)}내에 인증"
            } else {
                "00:00 ~ 23:00내에 인증" //시간 반영(QA 피드백)
            },
            "예치 도전금" to (selectedPoint ?: "-")
        )

        ChallengeInfoBox(
            height = 90.dp,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp)) {
                summaryItems.forEachIndexed { index, (label, value) ->
                    if (index > 0) Spacer(Modifier.height(spacing.spacing8))
                    ChallengeSummaryRow(label = label, value = value)
                }
            }
        }

        Spacer(Modifier.height(46.dp))

        val canStart = selectedWeeks != null && selectedDeposit != null

        OnulDoButton(
            text = "도전 시작하기",
            onClick = {
                // enabled=canStart로 이미 걸러지지만, 널 언팩은 방어적으로 처리 (포인트 검사는 서버가 수행)
                val weeks = selectedWeeks ?: return@OnulDoButton
                val deposit = selectedDeposit ?: return@OnulDoButton
                onStartClick(weeks, deposit)
            },
            enabled = canStart && !isSubmitting
        )

        Spacer(Modifier.height(42.dp))
    }

    if (showInsufficientDialog) {
        // 필요 포인트 = 선택한 도전금, 보유 포인트 = 지갑 잔액(ownedPoint)
        InsufficientPointDialog(
            onDismiss = onDismissInsufficient,
            onCharge = onChargePoint,
            ownedPoint = ownedPoint,
            requiredPoint = selectedDeposit ?: 0
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

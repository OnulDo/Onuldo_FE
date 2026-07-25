package com.example.onuldo_fe.ui.screen.challenge.detail
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.screen.challenge.detail.component.VerificationMethodCard
import com.example.onuldo_fe.ui.screen.challenge.detail.component.VerificationNoticeBottomSheet
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream


//챌린지 상세 화면
@Composable
fun DetailScreen(
    challenge: Challenge = Challenge(id = 0, title = "새벽 6시 기상", participantCount = 1234),
    // TODO: 아래 더미 데이터는 API 연동 시 교체 (파일 하단 dummy* 상수 참고)
    category: String = "생활루틴",
    summary: String = DUMMY_SUMMARY,
    benefits: List<ChallengeBenefit> = dummyBenefits,
    recommendations: List<String> = dummyRecommendations,
    verificationTitle: String = "이렇게 찍어주세요",
    verificationDescription: String = "침대와 개어진 이불 사진이 나오게 촬영하기",
    onBackClick: () -> Unit = {},
    onJoinClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 바텀시트는 화면 이동(Navigation)이 아니라 이 화면의 상태(State)
    var showNoticeSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // 헤더 — 참여 화면과 같은 56dp 높이
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
        }

        // 제목 + 카테고리 · 참여자
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 23.dp, bottom = 22.dp)
        ) {
            Text(
                text = challenge.title,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 30.sp,
                color = BlackBrown
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$category 챌린지",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    lineHeight = 22.sp,
                    color = DarkBrown
                )
                Text(
                    text = "참여자 %,d명".format(challenge.participantCount),
                    modifier = Modifier.padding(start = 8.dp),
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 20.sp,
                    color = DarkBrown
                )
            }
        }

        // 구분선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Persimmon.copy(alpha = 0.2f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // TODO: "이 챌린지는? / 하면 좋은 점 / 이런 분께 추천해요" 본문은 서버에서 json 제공
            //       내려올 예정. 현재는 렌더링(AnnotatedString 파싱)이 없어
            //       일반 Text(더미)로 표시.
            DetailSectionTitle("이 챌린지는?")
            Row(
                modifier = Modifier.padding(top = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 4.dp, height = 19.dp)
                        .background(Persimmon, RoundedCornerShape(4.dp))
                )
                Text(
                    text = summary,
                    modifier = Modifier.padding(start = 5.dp),
                    fontFamily = Pretendard,
                    fontSize = 13.sp,
                    lineHeight = 24.sp,
                    color = BlackBrown
                )
            }

            Spacer(Modifier.height(33.dp))
            DetailSectionTitle("하면 좋은 점")
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                benefits.forEach { benefit ->
                    Column {
                        // 갈색 제목
                        Text(
                            text = benefit.title,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = DarkBrown
                        )
                        // 제목 밑 설명 한 줄
                        Text(
                            text = benefit.description,
                            modifier = Modifier.padding(top = 2.dp),
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = BlackBrown
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            DetailSectionTitle("이런 분께 추천해요")
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                recommendations.forEach { recommendation ->
                    Text(
                        text = recommendation,
                        fontFamily = Pretendard,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = DarkBrown
                    )
                }
            }

            Spacer(Modifier.height(30.dp))
            VerificationMethodCard(
                title = verificationTitle,
                description = verificationDescription,
                onShowNotice = { showNoticeSheet = true }
            )
        }

        Spacer(Modifier.height(10.dp))

        // 참여하기 버튼
        OnulDoButton(
            text = "참여하기",
            onClick = onJoinClick,
            modifier = Modifier.padding(bottom = 48.dp)
        )
    }

    if (showNoticeSheet) {
        VerificationNoticeBottomSheet(
            challengeTitle = challenge.title,
            onDismiss = { showNoticeSheet = false }
        )
    }
}

@Composable
private fun DetailSectionTitle(text: String) {
    Text(
        text = text,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 40.sp,
        color = BlackBrown
    )
}

// "하면 좋은 점" 항목 (제목 + 설명 한 줄)
data class ChallengeBenefit(val title: String, val description: String)

// ===== 더미 데이터 — API 연동 시 교체 =====
private const val DUMMY_SUMMARY = "세상보다 먼저 하루를 여는 21일, 나만의 새벽 30분"

private val dummyBenefits = listOf(
    ChallengeBenefit("아침에 나만의 30분이 생겨요", "세상이 조용한 시간, 방해 없이 나에게 집중할 수 있어요"),
    ChallengeBenefit("마음에 여유가 생겨요", "허둥지둥 뛰는 아침 대신, 커피 한 잔의 여유를 챙겨요"),
    ChallengeBenefit("생체 리듬이 잡혀요", "일찍 일어나면 밤에 잠도 잘 오는 선순환이 만들어져요"),
    ChallengeBenefit("하루의 주도권을 되찾아요", "'시작 당한' 게 아니라 '시작한' 감각으로 살게 돼요")
)

private val dummyRecommendations = listOf(
    "미라클 모닝을 여러 번 시도했지만 매번 3일을 못 넘긴 분",
    "출근·등교 직전 늘 시간에 쫓기는 게 지겨운 분",
    "아침형 인간이 되고 싶어 자기계발을 시작하려는 분",
    "혼자서는 자꾸 무너져서 함께할 동료가 필요한 분"
)

@Preview(showBackground = true, widthDp = 390, heightDp = 1200)
@Composable
private fun DetailScreenPreview() {
    OnulDo_FETheme {
        DetailScreen()
    }
}

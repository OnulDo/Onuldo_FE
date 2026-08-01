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
import androidx.compose.material3.MaterialTheme
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
import com.example.onuldo_fe.ui.theme.LocalSpacing
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
    // 서버 인증 예시 사진 URL(verificationExamplePhotoUrl). null이면 카드에서 로컬 drawable 폴백.
    verificationImageUrl: String? = null,
    onBackClick: () -> Unit = {},
    onJoinClick: () -> Unit = {},
    // 파티 생성 흐름에서 상세 화면을 재사용할 때 CTA 문구만 변경할 수 있도록 외부에서 전달
    ctaText: String = "참여하기",
    modifier: Modifier = Modifier
) {
    // 바텀시트는 화면 이동(Navigation)이 아니라 이 화면의 상태(State)
    var showNoticeSheet by remember { mutableStateOf(false) }
    val spacing = LocalSpacing.current

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
                // 패딩 없이 정렬만 — IconButton 중앙정렬로 화살표가 가로 20에 맞음(본문과 정렬)
                modifier = Modifier.align(Alignment.CenterStart)
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
            // 아직 대응 API 필드가 없어 DetailDummyData.kt의 더미를 기본값으로 표시한다.
            if (summary.isNotBlank()) {
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
            }

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
                            style = MaterialTheme.typography.bodyMedium,  // Body3
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

            Spacer(Modifier.height(spacing.spacing30))
            VerificationMethodCard(
                title = verificationTitle,
                description = verificationDescription,
                imageUrl = verificationImageUrl,
                onShowNotice = { showNoticeSheet = true }
            )
        }

        Spacer(Modifier.height(spacing.spacing10))

        // 참여하기 버튼
        OnulDoButton(
            text = ctaText,
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

// 기본값/프리뷰용 더미 데이터는 DetailDummyData.kt 참고

@Preview(showBackground = true, widthDp = 390, heightDp = 1200)
@Composable
private fun DetailScreenPreview() {
    OnulDo_FETheme {
        DetailScreen()
    }
}

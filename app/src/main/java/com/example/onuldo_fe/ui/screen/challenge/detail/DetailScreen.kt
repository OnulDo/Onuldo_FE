package com.example.onuldo_fe.ui.screen.challenge.detail
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.model.challenge.ContentBlock
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.screen.challenge.detail.component.ChallengeContent
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
    challenge: Challenge = Challenge(id = 0L, title = "새벽 6시 기상", participantCount = 1234),
    category: String = "생활루틴",
    // 상세 본문 블록. 실데이터는 DetailRoute에서 주입, 미전달 시(프리뷰/파티) 더미 사용.
    content: List<ContentBlock> = dummyContentBlocks,
    verificationTitle: String = "이렇게 찍어주세요",
    verificationDescription: String = "침대와 개어진 이불 사진이 나오게 촬영하기",
    // 서버 인증 예시 사진 URL(verificationExamplePhotoUrl). null이면 카드에서 로컬 drawable 폴백.
    verificationImageUrl: String? = null,
    // 인증 유의사항 시트용 성공/실패 조건. 비어있으면(파티/프리뷰) 시트의 기본 더미가 쓰인다.
    successConditions: List<String> = emptyList(),
    failureConditions: List<String> = emptyList(),
    onBackClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    // 파티 생성 흐름에서 상세 화면을 재사용 가능하게 수정
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
            // 상세 본문 — 서버 description(블록 JSON)을 파싱한 content를 타입별로 렌더링
            ChallengeContent(blocks = content)

            Spacer(Modifier.height(27.dp))
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
            onClick = onActionClick,
            modifier = Modifier.padding(bottom = 48.dp)
        )
    }

    if (showNoticeSheet) {
        // 성공/실패 조건을 그대로 전달 — 비어 있는 쪽은 시트가 리스트별로 기본 조건으로 폴백한다.
        VerificationNoticeBottomSheet(
            challengeTitle = challenge.title,
            onDismiss = { showNoticeSheet = false },
            successConditions = successConditions,
            failureConditions = failureConditions
        )
    }
}

// 기본값/프리뷰용 더미 데이터는 DetailDummyData.kt 참고

@Preview(showBackground = true, widthDp = 390, heightDp = 1200)
@Composable
private fun DetailScreenPreview() {
    OnulDo_FETheme {
        DetailScreen()
    }
}

package com.example.onuldo_fe.ui.screen.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

/**
 * ② 챌린지 상세 화면 (challenge detail)
 *
 * ⚠️ 레이아웃 골격만 구현 — 정확한 여백/크기/폰트는 피그마 스펙 받으면 카드 때처럼 맞춤.
 * 상단 뒤로가기(OnulDoBackButton) / 하단 참여하기(OnulDoButton)는 공용 컴포넌트 재사용.
 */
@Composable
fun ChallengeDetailScreen(
    challenge: Challenge = sampleChallenges[0],
    category: String = "생활루틴 챌린지",        // TODO: 실제 카테고리 데이터
    onBackClick: () -> Unit = {},
    onShowVerificationNotice: () -> Unit = {},   // TODO: 인증 유의사항 바텀시트(도메인 협의 후)
    onJoinClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
    ) {
        // ── 상단 바 (뒤로가기) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OnulDoBackButton(onClick = onBackClick)
        }

        // ── 스크롤 본문 ──
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.labelLarge,
                color = DarkBrown50
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "%,d명 참여".format(challenge.participantCount),
                style = MaterialTheme.typography.labelMedium,
                color = DarkBrown50
            )

            Spacer(Modifier.height(24.dp))

            // 챌린지 정보
            Text(
                text = "챌린지 정보",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(White)
                    .border(1.dp, DarkBrown20, RoundedCornerShape(16.dp))
                // TODO: 챌린지 소개 이미지/설명 영역
            )

            Spacer(Modifier.height(24.dp))

            // 인증 방법
            Text(
                text = "인증 방법",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            VerificationMethodCard(onShowNotice = onShowVerificationNotice)

            Spacer(Modifier.height(24.dp))
        }

        // ── 하단 고정 버튼 ──
        OnulDoButton(
            text = "참여하기",
            onClick = onJoinClick,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

@Composable
private fun VerificationMethodCard(
    onShowNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Persimmon10)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO: 인증 방법 캐릭터 일러스트 (피그마 export 예정)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(White)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "이렇게 해보세요",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "촬영한 사진으로 챌린지를 인증해요",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkBrown50,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        // 인증 유의사항 보기 → 바텀시트 (도메인 협의 후 연결). onClick=onShowNotice 로 연결 예정
        Text(
            text = "인증 유의사항 보기 >",
            style = MaterialTheme.typography.labelLarge,
            color = DarkBrown50,
            modifier = Modifier.clickable(onClick = onShowNotice)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun ChallengeDetailScreenPreview() {
    OnulDo_FETheme {
        ChallengeDetailScreen()
    }
}

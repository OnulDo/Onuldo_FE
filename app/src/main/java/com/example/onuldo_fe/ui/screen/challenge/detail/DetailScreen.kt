package com.example.onuldo_fe.ui.screen.challenge.detail
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeInfoBox
import com.example.onuldo_fe.ui.screen.challenge.detail.component.VerificationMethodCard
import com.example.onuldo_fe.ui.screen.challenge.detail.component.VerificationNoticeBottomSheet
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream


//챌린지 상세 화면
@Composable
fun DetailScreen(
    challenge: Challenge = Challenge(id = 0, title = "새벽 6시 기상", participantCount = 1234),
    category: String = "생활루틴 챌린지",                              // TODO: 실제 카테고리 데이터 / 더미 데이터 — API 연동 시 교체
    verificationTitle: String = "이렇게 찍어주세요",                    // TODO: 실제 인증 방법 데이터 / 더미 데이터 — API 연동 시 교체
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
        Spacer(Modifier.height(25.dp))
        OnulDoBackButton(
            onClick = onBackClick,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(Modifier.height(25.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = category,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = DarkBrown
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = challenge.title,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 26.sp,
                color = BlackBrown
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "참여자 %,d명".format(challenge.participantCount),
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                color = DarkBrown
            )

            Spacer(Modifier.height(26.dp))

            Text(
                text = "챌린지 정보",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                color = BlackBrown
            )

            Spacer(Modifier.height(9.dp))

            ChallengeInfoBox(height = 192.dp)

            Spacer(Modifier.height(20.dp))

            VerificationMethodCard(
                title = verificationTitle,
                description = verificationDescription,
                onShowNotice = { showNoticeSheet = true }
            )
        }

        Spacer(Modifier.height(134.dp))

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

@Preview(showBackground = true, widthDp = 390, heightDp = 1048)
@Composable
private fun DetailScreenPreview() {
    OnulDo_FETheme {
        DetailScreen()
    }
}

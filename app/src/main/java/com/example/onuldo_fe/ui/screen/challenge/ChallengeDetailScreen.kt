package com.example.onuldo_fe.ui.screen.challenge

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White


//챌린지 상세 화면 : 페이지 전체가 스크롤

@Composable
fun ChallengeDetailScreen(
    challenge: Challenge = sampleChallenges[0],
    category: String = "생활루틴 챌린지",                              // TODO: 실제 카테고리 데이터
    verificationTitle: String = "이렇게 찍어주세요",                    // TODO: 실제 인증 방법 데이터
    verificationDescription: String = "침대와 개어진 이불 사진이 나오게 촬영하기",
    onBackClick: () -> Unit = {},
    onShowVerificationNotice: () -> Unit = {},   // TODO: 인증 유의사항 바텀시트(도메인 협의 후)
    onJoinClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // 디자인 기준: 상태바-화살표 25, 화살표-카테고리 42.
        // OnulDoBackButton은 48dp IconButton(14dp 화살표가 중앙) → 위아래 17dp 내부 여백만큼 빼서 보정.
        Spacer(Modifier.height(8.dp))  // 25 - 17
        OnulDoBackButton(
            onClick = onBackClick,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(Modifier.height(25.dp)) // 42 - 17

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp) // 좌우 20 → 내용 폭 350
        ) {
            // 생활루틴 챌린지 — Pretendard Bold 12, #5C2C03
            Text(
                text = category,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = DarkBrown
            )

            Spacer(Modifier.height(3.dp))

            // 새벽 6시 기상 — Pretendard Bold 26, #1B130C
            Text(
                text = challenge.title,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 26.sp,
                color = BlackBrown
            )

            Spacer(Modifier.height(12.dp))

            // 참여자 1,234명 — Pretendard Medium 11, #5C2C03
            Text(
                text = "참여자 %,d명".format(challenge.participantCount),
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                color = DarkBrown
            )

            Spacer(Modifier.height(26.dp))

            // 챌린지 정보 — Pretendard Bold 14
            Text(
                text = "챌린지 정보",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                color = BlackBrown
            )

            Spacer(Modifier.height(9.dp))

            // 백지 네모 350 x 192, 라운드 14, 1px 테두리 // 임시 (내용/테두리 색 미정)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(White)
                    .border(1.dp, DarkBrown20, RoundedCornerShape(14.dp)) // 임시
            )

            Spacer(Modifier.height(20.dp))

            // 인증 방법 창 350 x 348
            VerificationMethodCard(
                title = verificationTitle,
                description = verificationDescription,
                onShowNotice = onShowVerificationNotice
            )
        }

        Spacer(Modifier.height(134.dp)) // 인증 방법 - 참여하기 버튼

        // 참여하기 버튼 (공용 OnulDoButton, 높이 56)
        OnulDoButton(
            text = "참여하기",
            onClick = onJoinClick,
            modifier = Modifier.padding(bottom = 48.dp) // 버튼 - 바닥 48
        )
    }
}

/**
 * 인증 방법 창 — 350 x 348, 라운드 14, 배경 #FC6B2B1A, 테두리 1px #FC6B2B33.
 * 내부 좌우 여백 16.
 */
@Composable
private fun VerificationMethodCard(
    title: String,
    description: String,
    onShowNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(348.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Persimmon10)
            .border(1.dp, Persimmon20, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(20.dp)) // 카드 위 - 인증 방법

        // 인증 방법 — Pretendard Bold 13, #1B130C
        Text(
            text = "인증 방법",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            lineHeight = 13.sp,
            color = BlackBrown
        )

        Spacer(Modifier.height(23.dp))

        // 이렇게 찍어주세요 — Pretendard Bold 15, #FC6B2B
        Text(
            text = title,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            lineHeight = 15.sp,
            color = Persimmon
        )

        Spacer(Modifier.height(4.dp))

        // 설명 — Pretendard Regular 12, #1B130C
        Text(
            text = description,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 12.sp,
            color = BlackBrown
        )

        Spacer(Modifier.height(9.dp))

        // 흰 박스 318 x 170, 라운드 14, 테두리 1.5px #FC6B2B33
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(White)
                .border(1.5.dp, Persimmon20, RoundedCornerShape(14.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(15.dp))

            // 사진 캐릭터 104 x 116
            Image(
                painter = painterResource(R.drawable.challenge_camera_icon),
                contentDescription = null,
                modifier = Modifier.size(width = 104.dp, height = 116.dp)
            )

            Spacer(Modifier.height(2.dp))

            // 예시 사진을 추가해주세요 — Pretendard Medium 11, #5C2C0380, 가운데
            Text(
                text = "예시 사진을 추가해주세요",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                color = DarkBrown50,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(14.dp)) // 흰 박스 - 유의사항 박스

        // 인증 유의사항 보기 박스 318 x 44 (내부: 위 15 / 글씨 16 / 아래 13)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(White)
                .border(1.5.dp, Persimmon20, RoundedCornerShape(14.dp))
                .clickable(onClick = onShowNotice)
        ) {
            Spacer(Modifier.height(15.dp))
            Text(
                text = "인증 유의사항 보기",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                color = BlackBrown,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1048)
@Composable
private fun ChallengeDetailScreenPreview() {
    OnulDo_FETheme {
        ChallengeDetailScreen()
    }
}

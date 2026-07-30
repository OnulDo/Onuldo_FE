package com.example.onuldo_fe.ui.screen.verification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.screen.verification.VerificationStepCard.component.VerificationStepCard
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun VerificationReviewingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 27.dp)
                    .height(48.dp)
            ) {
                OnulDoBackButton(
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "인증 결과",
                    color = BlackBrown,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            Image(
                painter = painterResource(
                    id = R.drawable.verification_reviewing_icon
                ),
                contentDescription = "인증 검토 아이콘",
                modifier = Modifier
                    .padding(top = 57.dp)
                    .size(120.dp)
            )

            Text(
                text = "AI가 인증 사진을 확인하고 있어요",
                color = BlackBrown,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "잠시만 기다려주세요! (예상 소요 시간 5초)",
                color = DarkBrown70,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(42.dp))

            // 추후 1,2,3,4,5,6 -> 체크 표시로 넘어가는 애니메이션 작성 (현재는 체크로 일괄 표시)
            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "사진 메타데이터 검증",
            )

            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "AI 이미지 전송",
            )

            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "물체·활동 감지",
            )

            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "신뢰도 필터링",
            )

            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "챌린지 조건 분석",
            )

            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "최종 결과 판정",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VerificationReviewingScreenPreview() {
    OnulDo_FETheme {
        VerificationReviewingScreen()
    }
}
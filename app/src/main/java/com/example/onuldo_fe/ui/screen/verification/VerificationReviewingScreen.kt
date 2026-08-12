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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import kotlinx.coroutines.delay

@Composable
fun VerificationReviewingScreen(
    isResultReady: Boolean = false
) {
    val steps = listOf(
        "사진 메타데이터 검증",
        "AI 이미지 전송",
        "물체·활동 감지",
        "신뢰도 필터링",
        "챌린지 조건 분석",
        "최종 결과 판정"
    )
    var animatedStepCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        // 서버가 단계별 진행률을 제공하지 않으므로 앞의 5단계만 안내용으로 순차 표시한다.
        repeat(steps.lastIndex) { index ->
            delay(300L)
            animatedStepCount = index + 1
        }
    }

    val completedStepCount = when {
        animatedStepCount < steps.lastIndex -> animatedStepCount
        isResultReady -> steps.size
        else -> steps.lastIndex
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 27.dp).height(48.dp)
            ) {
                Text(
                    text = "인증 검토중",
                    color = BlackBrown,
                    style = OnulDoTypography.body3Bold,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            Image(
                painter = painterResource(id = R.drawable.verification_reviewing_icon),
                contentDescription = "인증 검증 중",
                modifier = Modifier.padding(top = 57.dp).size(120.dp)
            )
            Text(
                text = "AI가 인증 사진을 확인하고 있어요",
                color = BlackBrown,
                style = OnulDoTypography.body2Bold,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "잠시만 기다려주세요! (예상 소요 시간 약 5초)",
                color = DarkBrown70,
                style = OnulDoTypography.caption1Regular,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(42.dp))

            steps.forEachIndexed { index, title ->
                VerificationStepCard(
                    icon = R.drawable.verification_check_icon,
                    title = title,
                    completed = index < completedStepCount
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VerificationReviewingScreenPreview() {
    OnulDo_FETheme { VerificationReviewingScreen() }
}

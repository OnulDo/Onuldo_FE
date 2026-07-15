package com.example.onuldo_fe.ui.screen.verification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun ChallengeVerificationScreen(
    status: VerificationStatus
) {
    when (status) {
        VerificationStatus.REVIEWING -> {
            VerificationReviewingContent()
        }

        VerificationStatus.SUCCESS -> {
            VerificationSuccessContent()
        }

        VerificationStatus.FAILURE -> {
            VerificationFailureContent()
        }
        VerificationStatus.WAITING -> {
            VerificationWaitingContent()
        }
    }
}


@Composable
private fun VerificationReviewingContent() {
    // 검토 중 UI
}

@Composable
private fun VerificationSuccessContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
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
                    id = R.drawable.verification_success_icon
                ),
                contentDescription = "인증 성공 아이콘",
                modifier = Modifier
                    .padding(top = 243.dp)
                    .size(135.dp)
            )

            Text(
                text = "인증 성공!",
                color = BlackBrown,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "AI 검증을 모두 통과했어요",
                color = DarkBrown70,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 11.dp),
                textAlign = TextAlign.Center
            )
        }

        OnulDoButton(
            text = "확인",
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
        )
    }
}
@Composable
private fun VerificationFailureContent() {
    // 인증 실패 UI
}

@Composable
private fun VerificationWaitingContent() {
    // 검토 대기 UI
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun VerificationSuccessContentPreview() {
    OnulDo_FETheme {
        ChallengeVerificationScreen(
            status = VerificationStatus.SUCCESS
        )
    }
}

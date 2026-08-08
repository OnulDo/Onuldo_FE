package com.example.onuldo_fe.ui.screen.verification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
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
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import java.time.Duration
import java.time.LocalTime
import kotlinx.coroutines.delay

@Composable
fun VerificationFailureScreen(
    failureReason: String = "사진이 챌린지 인증 조건을 충족하지 못했어요.",
    verificationDeadline: String = "",
    onRetryClick: () -> Unit = {},
    onManualReviewClick: () -> Unit = {}
) {
    val remainingTimeText by produceState(
        initialValue = verificationDeadline.toRemainingTimeText(),
        key1 = verificationDeadline
    ) {
        while (true) {
            value = verificationDeadline.toRemainingTimeText()
            delay(60_000L)
        }
    }

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
                    id = R.drawable.verification_failure_icon
                ),
                contentDescription = "인증 실패 아이콘",
                modifier = Modifier
                    .padding(top = 57.dp)
                    .size(120.dp)
            )

            Text(
                text = "인증에 실패했어요",
                color = BlackBrown,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "AI 검증에서 미션 조건을 확인하지 못했어요",
                color = DarkBrown70,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 37.dp),
                shape = RoundedCornerShape(size = 14.dp),
                color = Red2
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "실패 사유",
                            color = Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(11.dp))
                    //실패사유 목데이터 연결하기.
                    Text(
                        text = failureReason.ifBlank { "사진이 챌린지 인증 조건을 충족하지 못했어요." },
                        color = BlackBrown,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                shape = RoundedCornerShape(size = 14.dp),
                color = Persimmon10,
                border = BorderStroke(
                    width = 1.dp,
                    color = Persimmon20
                )
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "남은 인증 시간",
                            color = Persimmon,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(9.dp))

                    Text(
                        text = remainingTimeText,
                        color = BlackBrown,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 22.dp)
                    )
                }
            }
            Text(
                text = "AI 판정에 동의하지 않으시나요?",
                color = DarkBrown70,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 28.dp)
            )
            Text(
                text = "직접검토 요청하기",
                color = Persimmon,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(top = 7.dp)
                    .clickable(onClick = onManualReviewClick)
            )
        }

        OnulDoButton(
            text = "다시 인증하기",
            onClick = onRetryClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
        )
    }
}

private fun String.toRemainingTimeText(now: LocalTime = LocalTime.now()): String {
    val deadline = runCatching { LocalTime.parse(this) }.getOrNull()
        ?: return "남은 인증 시간을 확인할 수 없어요."
    val remainingSeconds = Duration.between(now, deadline).seconds
    if (remainingSeconds <= 0L) return "오늘 인증 시간이 종료되었어요."

    val remainingMinutes = (remainingSeconds + 59L) / 60L
    val remainingTime = if (remainingMinutes <= 60L) {
        "${remainingMinutes}분"
    } else {
        "${remainingMinutes / 60L}시간"
    }
    return "$remainingTime 남았어요. 재인증해보세요!"
}

@Preview(showBackground = true)
@Composable
private fun VerificationFailureScreenPreview() {
    OnulDo_FETheme {
        VerificationFailureScreen()
    }
}

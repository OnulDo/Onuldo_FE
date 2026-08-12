package com.example.onuldo_fe.ui.screen.verification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.remember
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
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.White
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun VerificationWaitingScreen(
    submittedAt: String? = null,
    onConfirmClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    val submittedAtText = remember(submittedAt) { submittedAt.toDisplayDateTime() }

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
                    style = OnulDoTypography.body2Bold,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(spacing.spacing48))

            Image(
                painter = painterResource(id = R.drawable.verification_waiting_icon),
                contentDescription = "인증 대기 아이콘",
                modifier = Modifier
                    .size(120.dp)
            )
            Spacer(modifier = Modifier.height(spacing.spacing8))

            Text(
                text = "인증 검토 중이에요",
                color = BlackBrown,
                style = OnulDoTypography.title1Bold,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(spacing.spacing10))

            Text(
                text = "AI가 판단하기 어려운 사진이라\n운영팀에서 직접 확인하고 있어요.",
                color = DarkBrown70,
                style = OnulDoTypography.caption1Regular,
                textAlign = TextAlign.Center
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 59.dp), // 디자인시스템 미적용 (추후 다시 적용)
                shape = RoundedCornerShape(size = 14.dp),
                color = Persimmon10,
                border = BorderStroke(
                    width = 1.dp,
                    color = Persimmon20
                )
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "검토 안내",
                            color = BlackBrown,
                            style = OnulDoTypography.caption2Bold,
                        )
                    }
                    Spacer(modifier = Modifier.height(11.dp))

                    Text(
                        text = "검토는 최대 24시간 이내 완료됩니다.",
                        color = DarkBrown,
                        style = OnulDoTypography.caption3Regular,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )

                    Text(
                        text = "검토 중에도 챌린지는 계속 진행됩니다.",
                        color = DarkBrown,
                        style = OnulDoTypography.caption3Regular,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )

                    Text(
                        text = "결과는 알림으로 즉시 안내드려요.",
                        color = DarkBrown,
                        style = OnulDoTypography.caption3Regular,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )

                    Text(
                        text = "검토 통과 시 인증 완료 처리됩니다.",
                        color = DarkBrown,
                        style = OnulDoTypography.caption3Regular,
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.spacing16))

            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(size = 14.dp),
                color = White,
                border = BorderStroke(
                    width = 1.dp,
                    color = DarkBrown40
                )
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "제출 시각",
                            color = DarkBrown,
                            style = OnulDoTypography.caption3Bold,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = submittedAtText,
                            color = BlackBrown,
                            style = OnulDoTypography.caption2Bold,
                        )
                    }
                }
            }
        }

        OnulDoButton(
            text = "확인",
            onClick = onConfirmClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
        )
    }
}

private val submittedAtFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH:mm")

private fun String?.toDisplayDateTime(): String {
    if (this.isNullOrBlank()) return "제출 시각을 확인할 수 없어요"
    val dateTime = runCatching { LocalDateTime.parse(this) }.getOrNull()
        ?: runCatching { OffsetDateTime.parse(this).toLocalDateTime() }.getOrNull()
        ?: return "제출 시각을 확인할 수 없어요"
    return dateTime.format(submittedAtFormatter)
}

@Preview(showBackground = true)
@Composable
private fun VerificationWaitingScreenPreview() {
    OnulDo_FETheme {
        VerificationWaitingScreen()
    }
}

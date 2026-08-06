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
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.White

@Composable
fun VerificationWaitingScreen(
    onConfirmClick: () -> Unit = {}
) {
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
                painter = painterResource(id = R.drawable.verification_waiting_icon),
                contentDescription = "인증 대기 아이콘",
                modifier = Modifier
                    .padding(top = 57.dp)
                    .size(120.dp)
            )

            Text(
                text = "인증 검토 중이에요",
                color = BlackBrown,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "AI가 판단하기 어려운 사진이라\n운영팀에서 직접 확인하고 있어요.",
                color = DarkBrown70,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 61.dp),
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
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(11.dp))

                    Text(
                        text = "검토는 최대 24시간 이내 완료됩니다.",
                        color = DarkBrown,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )

                    Text(
                        text = "검토 중에도 챌린지는 계속 진행됩니다.",
                        color = DarkBrown,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )

                    Text(
                        text = "결과는 알림으로 즉시 안내드려요.",
                        color = DarkBrown,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )

                    Text(
                        text = "검토 통과 시 인증 완료 처리됩니다.",
                        color = DarkBrown,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
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
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "2026년 5월 20일 07:32",
                            color = BlackBrown,
                            style = MaterialTheme.typography.bodySmall,
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

@Preview(showBackground = true)
@Composable
private fun VerificationWaitingScreenPreview() {
    OnulDo_FETheme {
        VerificationWaitingScreen()
    }
}

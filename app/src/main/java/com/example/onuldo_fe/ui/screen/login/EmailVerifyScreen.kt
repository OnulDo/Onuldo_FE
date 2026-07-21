package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnboardingBackHeader
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import kotlinx.coroutines.delay

private const val RESEND_SECONDS = 60

/**
 * 이메일 인증 메일 발송 안내 — WF ver.2.2, 온보딩 2/4 (Figma node 4353:3269).
 * 공부 캐릭터 + "메일 보냈어요" 안내 + 확인 카드 + 재발송(60초)/확인 버튼.
 *
 * ⚠️ 실제 메일 발송은 하지 않는다(화면만). 재발송은 카운트다운만 동작(발송 API는 TODO).
 */
@Composable
fun EmailVerifyScreen(
    email: String,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    // 60초 카운트다운. 값이 바뀔 때마다 1초 뒤 감소 → 0에서 멈춘다. 재발송 시 60으로 리셋.
    var secondsLeft by remember { mutableIntStateOf(RESEND_SECONDS) }
    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }
    val canResend = secondsLeft == 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        OnboardingBackHeader(onBack = onBack)

        Spacer(Modifier.height(40.dp))

        Image(
            painter = painterResource(R.drawable.img_character_study),
            contentDescription = "인증 메일 발송 안내 캐릭터",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 165.dp, height = 203.dp),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${email}로 인증 메일을 보냈어요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Persimmon,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(16.dp))

        MailGuideCard(modifier = Modifier.padding(horizontal = 20.dp))

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // 재발송 (60초 대기)
            Button(
                onClick = { if (canResend) secondsLeft = RESEND_SECONDS /* TODO: 메일 재발송 API */ },
                enabled = canResend,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBrown10,
                    contentColor = DarkBrown,
                    disabledContainerColor = DarkBrown10,
                    disabledContentColor = DarkBrown,
                ),
            ) {
                Text(
                    text = if (canResend) "재발송" else "재발송 (${secondsLeft}초)",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }

            // 확인
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Persimmon),
            ) {
                Text(
                    text = "확인",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

/** "메일 확인 안내" 카드 — 인증 절차 3단계 안내. */
@Composable
private fun MailGuideCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(White, RoundedCornerShape(14.dp))
            .border(1.5.dp, DarkBrown40, RoundedCornerShape(14.dp))
            .padding(horizontal = 15.dp, vertical = 15.dp),
    ) {
        Text(
            text = "메일 확인 안내",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(10.dp))
        listOf(
            "1) 받은 메일함을 확인해주세요",
            "2) 메일 내 인증 링크를 클릭하면 완료",
            "3) 안 보이면 스팸함도 확인",
        ).forEach { line ->
            Text(
                text = line,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

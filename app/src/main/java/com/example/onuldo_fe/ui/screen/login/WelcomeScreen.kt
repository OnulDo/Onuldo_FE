package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 상태바(44) 아래 히어로 top 79 → 여백 35
        Spacer(Modifier.height(230.dp))

        HeroCard()

        Spacer(Modifier.height(20.dp))

        Text(
            text = "환영해요!",
            style = OnulDoTypography.header1ExtraBold,
            color = BlackBrown,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "오늘DO 가족이 되신 걸 축하드려요\n이제 첫 챌린지를 등록해볼까요?",
            style = OnulDoTypography.caption1Regular,
            color = DarkBrown70,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "가입 시 환영 보너스 100,000P가 함께 시작돼요",
            style = OnulDoTypography.caption1Bold,
            color = Persimmon
            )

        Spacer(Modifier.height(12.dp))

        OnulDoButton(
            text = "오늘DO 시작하기",
            onClick = onStart,
            textStyle = OnulDoTypography.startButton,
        )

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun HeroCard(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.auth_welcome_icon),
        contentDescription = null,
        modifier = modifier.size(135.dp),
    )
}
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WelcomeScreenPreview() {
    OnulDo_FETheme {
        WelcomeScreen(onStart = {})
    }
}

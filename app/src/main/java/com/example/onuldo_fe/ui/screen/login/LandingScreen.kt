package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.component.SecondaryButton
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Pretendard

@Composable
fun LandingScreen(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))

        // Figma 랜딩(4771:370)의 달리기 캐릭터(4771:383) = 248×248. 원본 에셋은 불꽃 상단까지 온전.
        Image(
            painter = painterResource(R.drawable.img_onuldo_character),
            contentDescription = "오늘두 캐릭터",
            modifier = Modifier.size(248.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "오늘DO",
            style = OnulDoTypography.brandLogo
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "혼자는 어렵잖아, 오늘두 함께!",
            color = DarkBrown,
            style = OnulDoTypography.brandSloganSemiBold
        )

        Spacer(Modifier.weight(1.4f))

        // Figma: 로그인=외곽선, 회원가입=채움.
        SecondaryButton(text = "로그인", onClick = onLoginClick)
        Spacer(Modifier.height(12.dp))
        OnulDoButton(text = "회원가입", onClick = onSignupClick)
        Spacer(Modifier.height(24.dp))
    }
}

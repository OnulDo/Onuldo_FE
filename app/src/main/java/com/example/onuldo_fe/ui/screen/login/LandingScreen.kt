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
import com.example.onuldo_fe.ui.theme.LocalSpacing

@Composable
fun LandingScreen(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 상단은 고정값으로 캐릭터를 Figma 위치(top 119)에 못박는다.
        // 비율(weight)로 나누면 기기 높이에 따라 캐릭터가 최대 100dp 넘게 움직이고,
        // 기준 기기(844)에서도 26dp 아래로 내려간다. 남는 공간은 아래 weight가 흡수한다.
        Spacer(Modifier.height(119.dp))

        // Figma 랜딩(4771:370)의 달리기 캐릭터(4771:383) = 248×248. 원본 에셋은 불꽃 상단까지 온전.
        Image(
            painter = painterResource(R.drawable.img_onuldo_character),
            contentDescription = "오늘두 캐릭터",
            modifier = Modifier.size(248.dp),
        )
        Spacer(Modifier.height(spacing.spacing12))
        Text(
            text = "오늘DO",
            style = OnulDoTypography.brandLogo
        )
        // 로고 아래 13 (Figma: 로고 360+31 → 슬로건 404)
        Spacer(Modifier.height(13.dp))
        Text(
            text = "혼자는 어렵잖아, 오늘두 함께!",
            color = DarkBrown,
            style = OnulDoTypography.brandSloganSemiBold
        )

        Spacer(Modifier.weight(1.4f))

        // Figma: 로그인=외곽선, 회원가입=채움. 버튼 간격 14(676+56 → 746), 하단 여백 42(802 → 844).
        SecondaryButton(text = "로그인", onClick = onLoginClick)
        Spacer(Modifier.height(spacing.spacing14))
        OnulDoButton(text = "회원가입", onClick = onSignupClick)
        Spacer(Modifier.height(42.dp))
    }
}

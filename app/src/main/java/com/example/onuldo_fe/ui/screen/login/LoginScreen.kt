package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.component.OnuldoTextField
import com.example.onuldo_fe.ui.component.SocialLoginButton
import com.example.onuldo_fe.ui.theme.DarkBrown30
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.KakaoLabel
import com.example.onuldo_fe.ui.theme.KakaoYellow
import com.example.onuldo_fe.ui.theme.NaverGreen
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red3
import com.example.onuldo_fe.viewmodel.LoginViewModel

/**
 * 이메일 로그인 화면 — WF ver.2.2 (Figma node 4353:3162).
 * 헤더(봉투 아이콘 + 타이틀) → 이메일/비밀번호 입력 → 로그인 → 비밀번호 찾기
 *  → "또는" 구분선 → 카카오/네이버 소셜 로그인 → 하단 회원가입 링크.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val isError = state.errorMessage != null

    // 좌우 20dp 여백은 버튼/필드가 자체적으로 갖고, 텍스트만 아래 modifier로 맞춘다.
    val gutter = Modifier.padding(horizontal = 20.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Spacer(Modifier.height(32.dp))

        Image(
            painter = painterResource(R.drawable.ic_email_badge),
            contentDescription = null,
            modifier = gutter.size(62.dp),
        )

        Spacer(Modifier.height(16.dp))
        Text(
            text = "로그인",
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = gutter,
        )

        Spacer(Modifier.height(28.dp))

        OnuldoTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "이메일",
            placeholder = "example@email.com",
            isError = isError,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        )

        Spacer(Modifier.height(16.dp))

        OnuldoTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "비밀번호",
            placeholder = "비밀번호를 입력해주세요",
            isPassword = true,
            isError = isError,
            supportingText = state.errorMessage,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        )

        if (isError) {
            Spacer(Modifier.height(10.dp))
            LoginErrorBanner(text = "로그인 정보를 다시 확인해주세요.", modifier = gutter)
        }

        Spacer(Modifier.height(24.dp))

        OnulDoButton(
            text = "로그인",
            onClick = { viewModel.login(onLoginSuccess) },
            enabled = state.isLoginEnabled,
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = "비밀번호 찾기",
            style = MaterialTheme.typography.labelLarge,
            color = DarkBrown50,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { /* TODO: 비밀번호 재설정 화면 */ },
        )

        Spacer(Modifier.height(28.dp))
        OrDivider(modifier = gutter)
        Spacer(Modifier.height(20.dp))

        SocialLoginButton(
            text = "카카오 아이디로 로그인",
            containerColor = KakaoYellow,
            contentColor = KakaoLabel,
            leadingIcon = R.drawable.ic_kakao,
            onClick = { /* TODO: 카카오 OAuth */ },
        )
        Spacer(Modifier.height(12.dp))
        SocialLoginButton(
            text = "네이버 아이디로 로그인",
            containerColor = NaverGreen,
            contentColor = Color.White,
            leadingIcon = R.drawable.ic_naver,
            onClick = { /* TODO: 네이버 OAuth */ },
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "아직 계정이 없으신가요? ",
                style = MaterialTheme.typography.labelLarge,
                color = DarkBrown50,
            )
            Text(
                text = "회원가입",
                style = MaterialTheme.typography.labelLarge,
                color = Persimmon,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onSignupClick),
            )
        }
    }
}

@Composable
private fun OrDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = DarkBrown30)
        Text(
            text = "또는",
            style = MaterialTheme.typography.labelMedium,
            color = DarkBrown50,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = DarkBrown30)
    }
}

/** 로그인 실패 배너 (설계 에러 화면). */
@Composable
private fun LoginErrorBanner(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Red3, RoundedCornerShape(12.dp))
            .border(1.dp, Red, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Red, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            color = Red,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

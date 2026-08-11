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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.R
import com.example.onuldo_fe.data.auth.dto.SocialProvider
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.component.OnuldoTextField
import com.example.onuldo_fe.ui.component.SocialLoginButton
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.KakaoLabel
import com.example.onuldo_fe.ui.theme.KakaoYellow
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.viewmodel.LoginViewModel

/**
 * 이메일 로그인 화면 — Figma Ready for Dev node `4771:384`.
 * 헤더(봉투 아이콘 + 타이틀) → 이메일/비밀번호 입력 → 로그인
 *  → "또는" 구분선 → 카카오 로그인 → 하단 회원가입 링크.
 *
 * 네이버 로그인은 제거됐다(2026-08-11). 자세한 경위는 [SocialAuthClient] 주석 참고.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    // 소셜 로그인 결과 신규 회원이면 약관 동의 화면으로 보낸다.
    onSocialSignupNeeded: () -> Unit = {},
    /**
     * 소셜 가입이 "이미 가입된 계정"으로 막혀 이 화면으로 되돌아오며 전달된 안내 문구.
     * 이 값이 들어오면 다음 소셜 로그인은 계정 선택 화면을 거친다.
     */
    existingAccountNotice: String? = null,
    /**
     * [existingAccountNotice]를 배너에 반영한 뒤 호출된다. 호출부는 여기서 값을 비워야 한다 —
     * 남겨 두면 같은 문구가 다시 전달될 때 값이 변하지 않아 배너가 뜨지 않는다.
     */
    onNoticeShown: () -> Unit = {},
    viewModel: LoginViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(existingAccountNotice) {
        existingAccountNotice?.let {
            viewModel.showExistingAccountNotice(it)
            onNoticeShown()
        }
    }
    val isError = state.errorMessage != null
    // 소셜 SDK는 로그인 창을 띄우기 위해 Activity 컨텍스트가 필요하다.
    val context = LocalContext.current

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
        // Figma(RFD) 타이틀 = Pretendard ExtraBold 28px (headlineLarge 토큰과 동일).
        Text(
            text = "로그인",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = gutter,
        )

        Spacer(Modifier.height(28.dp))

        // 이메일 칸은 오류 시에도 기본 테두리를 유지한다(Figma `4771:563`).
        // 설계서상 이메일·비밀번호 중 무엇이 틀렸는지 알려주지 않으므로 특정 칸을 지목하지 않는다.
        OnuldoTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "이메일",
            placeholder = "example@email.com",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        )

        Spacer(Modifier.height(16.dp))

        // 오류 문구는 비밀번호 칸 아래 헬퍼 텍스트로 붙는다(Figma `4771:563`).
        // 서버 문구를 그대로 노출하므로 5회 실패 잠금 안내 등도 이 자리에 들어온다.
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

        Spacer(Modifier.height(24.dp))

        OnulDoButton(
            text = "로그인",
            onClick = { viewModel.login(onLoginSuccess) },
            enabled = state.isLoginEnabled,
        )

        // '비밀번호 찾기'는 제거됨(2026-08-04 팀 확정).
        // 온보딩에서 이메일 인증 단계를 없앴기 때문에 본인 확인 수단이 없어 재설정을 구현할 수 없다.

        Spacer(Modifier.height(28.dp))
        OrDivider(modifier = gutter)
        Spacer(Modifier.height(20.dp))

        SocialLoginButton(
            text = if (state.socialInProgress == SocialProvider.KAKAO) {
                "카카오 로그인 중..."
            } else {
                "카카오 로그인"
            },
            containerColor = KakaoYellow,
            contentColor = KakaoLabel,
            leadingIcon = R.drawable.ic_kakao,
            iconSize = 28.dp,
            onClick = {
                viewModel.loginWithSocial(
                    context = context,
                    provider = SocialProvider.KAKAO,
                    onLoggedIn = onLoginSuccess,
                    onNeedSignup = onSocialSignupNeeded,
                )
            },
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
                color = DarkBrown70,
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
        HorizontalDivider(modifier = Modifier.weight(1f), color = DarkBrown50)
        Text(
            text = "또는",
            style = MaterialTheme.typography.labelMedium,
            color = DarkBrown70,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = DarkBrown50)
    }
}


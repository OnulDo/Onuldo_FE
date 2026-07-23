package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.component.OnboardingBackHeader
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.component.OnuldoTextField
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.SignupViewModel

/**
 * 회원가입(계정 만들기) — Figma Ready-for-Dev (node 5154:4298).
 * 뒤로가기 헤더 → 이메일/비밀번호/비밀번호 확인(실시간 유효성) → 환영 보너스 안내
 *  → 전체 약관 동의 카드 → "계속". 완료 시 프로필 설정으로 진행.
 */
@Composable
fun SignupScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: SignupViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val gutter = Modifier.padding(horizontal = 20.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        OnboardingBackHeader(onBack = onBack)

        Spacer(Modifier.height(24.dp))
        // Figma(RFD) 타이틀 = Pretendard ExtraBold 28px (headlineLarge 토큰과 동일).
        Text(
            text = "계정 만들기",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = gutter,
        )

        Spacer(Modifier.height(28.dp))

        OnuldoTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "이메일",
            placeholder = "example@email.com",
            isSuccess = state.emailSuccess,
            isError = state.emailError,
            supportingText = state.emailSupport,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        )

        Spacer(Modifier.height(12.dp))

        OnuldoTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "비밀번호",
            placeholder = "영문·숫자·특수문자 8~20자",
            isPassword = true,
            isError = state.passwordError,
            supportingText = state.passwordSupport,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
        )

        Spacer(Modifier.height(12.dp))

        OnuldoTextField(
            value = state.passwordConfirm,
            onValueChange = viewModel::onPasswordConfirmChange,
            label = "비밀번호 확인",
            placeholder = "비밀번호를 다시 입력해주세요",
            isPassword = true,
            isError = state.confirmError,
            supportingText = state.confirmSupport,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "가입 시 환영 보너스 100,000P가 함께 시작돼요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = Persimmon,
            modifier = gutter,
        )

        Spacer(Modifier.height(8.dp))

        AgreeAllCard(
            checked = state.agreeAll,
            onToggle = viewModel::toggleAgreeAll,
            modifier = gutter,
        )

        Spacer(Modifier.weight(1f))

        OnulDoButton(
            text = "계속",
            onClick = { viewModel.submit(onNext) },
            enabled = state.isContinueEnabled,
        )
        Spacer(Modifier.height(24.dp))
    }
}

/** 전체 약관 동의 카드 (체크박스 + 약관 링크). 하위 약관 상세는 추후 TODO. */
@Composable
private fun AgreeAllCard(
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Persimmon10, RoundedCornerShape(14.dp))
            .clickable(onClick = onToggle)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AgreeCheckbox(checked = checked)
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = "전체 약관에 동의합니다",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = termsLinkText(),
                fontFamily = Pretendard,
                fontSize = 11.sp,
                color = DarkBrown70,
            )
        }
    }
}

private fun termsLinkText() = buildAnnotatedString {
    withStyle(SpanStyle(color = Persimmon, textDecoration = TextDecoration.Underline)) {
        append("서비스 이용약관 ›")
    }
    append("   ")
    withStyle(SpanStyle(color = Persimmon, textDecoration = TextDecoration.Underline)) {
        append("개인정보 처리방침 ›")
    }
    append("   만 14세 이상")
}

/** 아이콘 의존성 없이 Canvas로 그린 체크박스. */
@Composable
private fun AgreeCheckbox(checked: Boolean) {
    val shape = RoundedCornerShape(7.dp)
    if (checked) {
        Canvas(
            modifier = Modifier
                .size(24.dp)
                .background(Persimmon, shape),
        ) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(w * 0.24f, h * 0.52f)
                lineTo(w * 0.42f, h * 0.70f)
                lineTo(w * 0.76f, h * 0.32f)
            }
            drawPath(
                path = path,
                color = White,
                style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
    } else {
        Spacer(
            modifier = Modifier
                .size(24.dp)
                .background(White, shape)
                .border(1.5.dp, DarkBrown40, shape),
        )
    }
}

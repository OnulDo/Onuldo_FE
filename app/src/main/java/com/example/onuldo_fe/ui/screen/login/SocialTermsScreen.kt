package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.data.auth.dto.TermType
import com.example.onuldo_fe.ui.component.OnboardingBackHeader
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.viewmodel.OnboardingDraft

/**
 * 소셜 신규 회원 약관 동의 화면.
 *
 * 이메일 가입은 회원가입 화면에서 약관 동의를 받지만, 소셜 로그인은 그 화면을 거치지 않는다.
 * 반면 서버 `POST /api/auth/oauth/signup`은 `termAgreements`를 필수로 요구하므로,
 * `isNewUser=true`인 경우 이 화면에서 동의를 받고 프로필 설정으로 넘어간다.
 *
 * 필수 약관은 서비스 이용약관·개인정보 처리방침·만 14세 이상 3종이다.
 */
@Composable
fun SocialTermsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onTermClick: (TermType) -> Unit = {},
) {
    var agreed by remember { mutableStateOf(OnboardingDraft.agreedRequiredTerms) }
    val gutter = Modifier.padding(horizontal = 20.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        OnboardingBackHeader(onBack = onBack)

        Spacer(Modifier.height(20.dp))

        Text(
            text = "약관에 동의해주세요",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = gutter,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "서비스 이용을 위해 아래 약관 동의가 필요해요",
            fontFamily = Pretendard,
            fontSize = 13.sp,
            color = DarkBrown70,
            modifier = gutter,
        )

        Spacer(Modifier.height(28.dp))

        AgreeAllRow(
            checked = agreed,
            onToggle = { agreed = !agreed },
            modifier = gutter,
        )

        Spacer(Modifier.height(16.dp))

        TermLinkRow("서비스 이용약관", { onTermClick(TermType.SERVICE) }, gutter)
        TermLinkRow("개인정보 처리방침", { onTermClick(TermType.PRIVACY) }, gutter)

        Spacer(Modifier.height(8.dp))
        Text(
            text = "만 14세 이상입니다",
            fontFamily = Pretendard,
            fontSize = 12.sp,
            color = DarkBrown70,
            modifier = gutter,
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "가입 시 환영 보너스 100,000P가 함께 시작돼요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = Persimmon,
            modifier = gutter,
        )

        Spacer(Modifier.height(12.dp))

        OnulDoButton(
            text = "동의하고 계속",
            onClick = {
                OnboardingDraft.setAgreedRequiredTerms(true)
                onNext()
            },
            enabled = agreed,
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AgreeAllRow(
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
        Text(
            text = "필수 약관에 모두 동의합니다",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun TermLinkRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontFamily = Pretendard,
            fontSize = 13.sp,
            color = DarkBrown70,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "›",
            fontFamily = Pretendard,
            fontSize = 15.sp,
            color = Persimmon,
        )
    }
}

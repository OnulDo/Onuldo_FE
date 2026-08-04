package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.data.auth.dto.TermType
import com.example.onuldo_fe.ui.component.OnboardingBackHeader
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.Black
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown30
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.OnboardingDraft

/**
 * 화면에 노출하는 약관 항목. 모두 필수이며 [detail]이 있으면 본문 화면으로 이동한다.
 * "만 14세 이상"은 별도 문서가 없어 이동 없이 체크만 한다.
 */
private enum class AgreeItem(val label: String, val detail: TermType?) {
    AGE_14("만 14세 이상입니다", null),
    SERVICE("서비스 이용약관 동의", TermType.SERVICE),
    PRIVACY("개인정보 처리방침 동의", TermType.PRIVACY),
    REFUND("환급 정책 동의", TermType.REFUND),
}

/**
 * 약관 동의 화면 — Figma node `5580:3391`.
 *
 * 이메일 회원가입과 소셜 신규 가입이 **공통으로** 거치는 단계다.
 * 서버 `POST /api/auth/signup`·`oauth/signup`이 `termAgreements`를 필수로 요구하므로
 * 여기서 동의를 받아 [OnboardingDraft]에 기록하고 프로필 설정으로 넘어간다.
 *
 * 회원가입 화면에 있던 "전체 약관에 동의합니다" 카드는 이 화면으로 대체됐다.
 */
@Composable
fun TermsAgreementScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onTermClick: (TermType) -> Unit = {},
) {
    // 항목별 동의 상태. 전체 동의는 개별 항목이 모두 체크됐는지로 판단한다.
    var checked by remember { mutableStateOf(emptySet<AgreeItem>()) }
    val allChecked = checked.size == AgreeItem.entries.size

    fun toggle(item: AgreeItem) {
        checked = if (item in checked) checked - item else checked + item
    }

    fun toggleAll() {
        checked = if (allChecked) emptySet() else AgreeItem.entries.toSet()
    }

    val gutter = Modifier.padding(horizontal = 20.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        OnboardingBackHeader(onBack = onBack)

        Spacer(Modifier.height(36.dp))

        Text(
            text = "약관에 동의해주세요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Black,
            fontSize = 26.sp,
            lineHeight = 36.sp,
            color = BlackBrown,
            modifier = gutter,
        )

        Spacer(Modifier.height(7.dp))

        Text(
            text = "서비스 이용을 위해 아래 약관에 동의해주세요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = DarkBrown70,
            modifier = gutter,
        )

        Spacer(Modifier.height(32.dp))

        AgreeAllCard(checked = allChecked, onToggle = ::toggleAll, modifier = gutter)

        Spacer(Modifier.height(16.dp))

        HorizontalDivider(color = DarkBrown30, modifier = gutter)

        Spacer(Modifier.height(20.dp))

        Column(modifier = gutter) {
            AgreeItem.entries.forEach { item ->
                AgreeItemRow(
                    item = item,
                    checked = item in checked,
                    onToggle = { toggle(item) },
                    onDetailClick = { item.detail?.let(onTermClick) },
                )
            }
        }

        Spacer(Modifier.weight(1f))

        OnulDoButton(
            text = "동의하고 계속",
            onClick = {
                OnboardingDraft.setAgreedRequiredTerms(true)
                onNext()
            },
            enabled = allChecked,
            fontSize = 17.sp,
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AgreeAllCard(
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Persimmon10)
            .clickable(onClick = onToggle)
            .padding(horizontal = 19.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundCheckbox(checked = checked, size = 24.dp)
        Spacer(Modifier.width(15.dp))
        Column {
            Text(
                text = "전체 동의",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                color = Black,
            )
            Text(
                text = "필수 및 선택 항목에 모두 동의합니다",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = DarkBrown70,
            )
        }
    }
}

@Composable
private fun AgreeItemRow(
    item: AgreeItem,
    checked: Boolean,
    onToggle: () -> Unit,
    onDetailClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(start = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.clickable(onClick = onToggle)) {
            RoundCheckbox(checked = checked, size = 22.dp)
        }
        Spacer(Modifier.width(15.dp))
        Text(
            text = "[필수]",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Persimmon,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = item.label,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = BlackBrown,
            modifier = Modifier.weight(1f),
        )
        // 본문이 있는 항목만 이동 화살표를 둔다.
        if (item.detail != null) {
            Image(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = "${item.label} 보기",
                modifier = Modifier
                    .clickable(onClick = onDetailClick)
                    .size(width = 5.dp, height = 8.dp),
            )
        }
    }
}

/** 원형 체크박스. 미체크는 테두리만, 체크는 Persimmon 채움. */
@Composable
private fun RoundCheckbox(checked: Boolean, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (checked) Persimmon else White)
            .then(
                if (checked) Modifier else Modifier.border(1.5.dp, DarkBrown40, CircleShape)
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Text(
                text = "✓",
                color = Color.White,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.55f).sp,
            )
        }
    }
}

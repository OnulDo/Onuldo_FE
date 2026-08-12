package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.data.auth.dto.TermType
import com.example.onuldo_fe.ui.component.OnboardingBackHeader
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.Black
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown30
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.viewmodel.OnboardingDraft

/**
 * 화면에 노출하는 약관 항목. [detail]이 있으면 본문 화면으로 이동한다.
 * "만 14세 이상"은 별도 문서가 없어 이동 없이 체크만 한다.
 *
 * [required]는 화면설계서 회원가입(`3766:6295`) 항목 4 기준 —
 * 화면에 노출되는 약관은 모두 필수이며 하나라도 미동의하면 완료 버튼을 비활성화한다.
 */
private enum class AgreeItem(
    val label: String,
    val detail: TermType?,
    val required: Boolean,
) {
    AGE_14("만 14세 이상입니다", null, required = true),
    SERVICE("서비스 이용약관 동의", TermType.SERVICE, required = true),
    PRIVACY("개인정보 처리방침 동의", TermType.PRIVACY, required = true),
    REFUND("환급 정책 동의", TermType.REFUND, required = true),
}

/**
 * 약관 상세로 이동했다가 뒤로 돌아와도 체크 상태를 유지하기 위한 Saver
 */
private val AgreeCheckedSaver = listSaver<Set<AgreeItem>, Int>(
    save = { checked -> checked.map(AgreeItem::ordinal) },
    restore = { ordinals -> ordinals.map { AgreeItem.entries[it] }.toSet() },
)

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
    // 약관 상세로 이동하면 이 화면이 컴포지션에서 빠져 remember가 초기화되므로,
    // 뒤로 돌아와도 체크가 유지되도록 rememberSaveable로 보존
    var checked by rememberSaveable(stateSaver = AgreeCheckedSaver) {
        mutableStateOf(emptySet<AgreeItem>())
    }
    val allChecked = checked.size == AgreeItem.entries.size
    // 화면의 모든 약관이 필수이므로 전부 체크해야 진행할 수 있다.
    val requiredChecked = AgreeItem.entries.filter { it.required }.all { it in checked }

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
            style = MaterialTheme.typography.headlineLarge,
            color = BlackBrown,
            modifier = gutter,
        )

        Spacer(Modifier.height(7.dp))

        Text(
            text = "서비스 이용을 위해 아래 약관에 동의해주세요",
            style = MaterialTheme.typography.bodyMedium,
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
            enabled = requiredChecked,
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
        AgreementAllCheckbox(checked = checked)
        Spacer(Modifier.width(15.dp))
        Column {
            Text(
                text = "전체 동의",
                style = MaterialTheme.typography.bodyLarge,
                color = Black,
            )
            Text(
                // Figma `5580:3404` 문구. 현재 화면의 4개 항목은 모두 필수이고 선택 항목은
                // 마케팅 수신 동의(V2)라 아직 없다 — 항목이 추가되면 이 문구와 맞아떨어진다.
                text = "필수 및 선택 항목에 모두 동의합니다",
                style = MaterialTheme.typography.labelLarge,
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
            TermCheckbox(checked = checked)
        }
        Spacer(Modifier.width(15.dp))
        // 필수는 Persimmon으로 강조, 선택은 본문과 같은 톤으로 낮춘다.
        Text(
            text = if (item.required) "[필수]" else "[선택]",
            style = MaterialTheme.typography.bodyMedium,
            color = if (item.required) Persimmon else DarkBrown70,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium,
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

/** 상태별 Vector Drawable을 사용하는 약관 체크박스. */
@Composable
private fun AgreementAllCheckbox(checked: Boolean) {
    Image(
        painter = painterResource(
            if (checked) R.drawable.auth_checkbox_checked
            else R.drawable.auth_checkbox_unchecked
        ),
        contentDescription = if (checked) "선택됨" else "선택 안 됨",
        modifier = Modifier.size(24.dp),
    )
}

/** 개별 약관 항목 전용 체크박스. */
@Composable
private fun TermCheckbox(checked: Boolean) {
    Image(
        painter = painterResource(
            if (checked) R.drawable.auth_term_checkbox_checked
            else R.drawable.auth_term_checkbox_unchecked
        ),
        contentDescription = if (checked) "선택됨" else "선택 안 됨",
        modifier = Modifier.size(22.dp),
    )
}

@Preview(
    name = "전체 동의 카드 - 체크 전후",
    showBackground = true,
    backgroundColor = 0xFFFFFDF7,
    widthDp = 390,
)
@Composable
private fun AgreeAllCardPreview() {
    OnulDo_FETheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            AgreeAllCard(
                checked = false,
                onToggle = {},
            )
            Spacer(Modifier.height(16.dp))
            AgreeAllCard(
                checked = true,
                onToggle = {},
            )
        }
    }
}

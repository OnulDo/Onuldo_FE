package com.example.onuldo_fe.ui.screen.mypage

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.component.AuthErrorBanner
import com.example.onuldo_fe.ui.screen.mypage.component.AmountChip
import com.example.onuldo_fe.ui.screen.mypage.component.AmountInputBox
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.ui.screen.mypage.component.PointCtaButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.utils.formatPoint
import com.example.onuldo_fe.viewmodel.mypage.PointChargeViewModel

private data class AmountPreset(val label: String, val value: Int)

private val chargePresets = listOf(
    AmountPreset("+10,000", 10_000),
    AmountPreset("+30,000", 30_000),
    AmountPreset("+50,000", 50_000),
    AmountPreset("+100,000", 100_000),
)

private data class PayMethod(val emoji: String, val name: String)

private val payMethods = listOf(
    PayMethod("💛", "토스페이"),
    PayMethod("💳", "신용/체크카드"),
    PayMethod("🏦", "계좌이체"),
)

/**
 * 포인트 충전 (v2) — Figma node `4019:4241`.
 * 금액 입력 + 프리셋 칩 + 결제 수단 선택.
 *
 * 충전은 `POST /wallet/charges`로 처리한다.
 * 결제 수단은 서버가 받지 않아 표시용이다 — TODO: 실제 결제(PG) 연동.
 */
@Composable
fun PointChargeScreen(
    onBack: () -> Unit,
    viewModel: PointChargeViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    // 칩은 누를 때마다 금액을 더한다(Figma 5154:3439). 0에서 시작해 사용자가 쌓아 올린다.
    var amount by remember { mutableIntStateOf(0) }
    var selectedMethod by remember { mutableIntStateOf(0) } // 기본 토스페이

    val amountText = "%,d".format(amount)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        MyPageTopBar(title = "충전", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "보유 ${formatPoint(state.balance)}",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = MySubText,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "얼마 충전할까요?",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = BlackBrown,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(24.dp))
            AmountInputBox(amount = amountText, unit = "원")

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                chargePresets.forEach { preset ->
                    AmountChip(
                        text = preset.label,
                        onClick = { amount += preset.value },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(30.dp))
            Text(
                text = "결제할 방법",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = BlackBrown,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(12.dp))
            payMethods.forEachIndexed { i, method ->
                PaymentMethodRow(
                    method = method,
                    selected = selectedMethod == i,
                    onClick = { selectedMethod = i },
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = "ⓘ  결제는 안전하게 처리돼요",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = MySubText,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            state.errorMessage?.let { message ->
                Spacer(Modifier.height(12.dp))
                AuthErrorBanner(text = message, modifier = Modifier.padding(horizontal = 20.dp))
            }

            Spacer(Modifier.height(24.dp))
        }

        PointCtaButton(
            text = if (state.isLoading) "충전 중..." else "충전하기",
            // 금액을 하나도 고르지 않으면 충전할 수 없다.
            enabled = amount > 0 && !state.isLoading,
            onClick = { viewModel.charge(amount, onBack) },
        )
    }
}

@Composable
private fun PaymentMethodRow(
    method: PayMethod,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) Persimmon else MyLine,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = method.emoji, fontSize = 22.sp)
        Spacer(Modifier.size(10.dp))
        Text(
            text = method.name,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = BlackBrown,
            modifier = Modifier.weight(1f),
        )
        SelectCircle(selected = selected)
    }
}

@Composable
private fun SelectCircle(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (selected) Persimmon else White)
            .then(if (selected) Modifier else Modifier.border(1.5.dp, MyLine, CircleShape)),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Text(text = "✓", fontSize = 14.sp, color = White)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PointChargeScreenPreview() {
    OnulDo_FETheme {
        PointChargeScreen(onBack = {})
    }
}

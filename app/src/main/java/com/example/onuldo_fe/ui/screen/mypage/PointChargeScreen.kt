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
import com.example.onuldo_fe.ui.screen.mypage.component.AmountChip
import com.example.onuldo_fe.ui.screen.mypage.component.AmountInputBox
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.ui.screen.mypage.component.PointCtaButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

private data class AmountPreset(val label: String, val value: Int)

private val chargePresets = listOf(
    AmountPreset("+1만", 10_000),
    AmountPreset("+3만", 30_000),
    AmountPreset("+5만", 50_000),
    AmountPreset("+10만", 100_000),
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
 * 값은 더미. TODO: 실제 결제(PG) 연동.
 */
@Composable
fun PointChargeScreen(
    onBack: () -> Unit,
) {
    var selectedPreset by remember { mutableIntStateOf(1) } // 기본 +3만 = 30,000
    var selectedMethod by remember { mutableIntStateOf(0) } // 기본 토스페이

    val amount = chargePresets[selectedPreset].value
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
                text = "보유 52,000P",
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
                chargePresets.forEachIndexed { i, preset ->
                    AmountChip(
                        text = preset.label,
                        selected = selectedPreset == i,
                        onClick = { selectedPreset = i },
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
            Spacer(Modifier.height(24.dp))
        }

        PointCtaButton(
            text = "${amountText}원 충전",
            enabled = true,
            onClick = { /* TODO: 결제 처리 후 지갑으로 복귀 */ onBack() },
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

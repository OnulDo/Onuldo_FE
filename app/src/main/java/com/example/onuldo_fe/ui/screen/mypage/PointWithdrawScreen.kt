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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

private data class WithdrawPreset(val label: String, val value: Int)

private val withdrawPresets = listOf(
    WithdrawPreset("전액", 45_000),
    WithdrawPreset("10,000P", 10_000),
    WithdrawPreset("30,000P", 30_000),
    WithdrawPreset("45,000P", 45_000),
)

/**
 * 포인트 출금 — Figma node `5154:3517`.
 * 출금 가능 금액 + 금액 입력/칩 + 보낼 곳.
 *
 * 출금은 충전과 달리 칩이 **금액을 지정**한다(가산이 아님).
 * 값은 더미. TODO: 서버에 출금 API가 없어 실제 이체는 미연동.
 */
@Composable
fun PointWithdrawScreen(
    onBack: () -> Unit,
) {
    // 0에서 시작해 칩으로 금액을 지정한다(Figma 기본 상태가 0P).
    var selectedPreset by remember { mutableStateOf<Int?>(null) }

    val amount = selectedPreset?.let { withdrawPresets[it].value } ?: 0
    val amountText = "%,d".format(amount)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        MyPageTopBar(title = "출금", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(24.dp))
            AvailableCard()

            Spacer(Modifier.height(32.dp))
            Text(
                text = "얼마 출금할까요?",
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
                withdrawPresets.forEachIndexed { i, preset ->
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
                text = "보낼 곳",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = BlackBrown,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(12.dp))
            AccountCard()

            Spacer(Modifier.height(20.dp))
            ArrivalInfo()

            Spacer(Modifier.height(24.dp))
        }

        PointCtaButton(
            text = "출금하기",
            enabled = amount > 0,
            onClick = { /* TODO: 서버 출금 API 추가 후 연결 */ onBack() },
        )
    }
}

@Composable
private fun AvailableCard() {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFEBE0))
            .padding(20.dp),
    ) {
        Text(
            text = "출금할 수 있는 금액",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Persimmon,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "45,000원",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = BlackBrown,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "보유 52,000P · 진행 중 7,000P",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = MySubText,
        )
    }
}

@Composable
private fun AccountCard() {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, MyLine, RoundedCornerShape(16.dp))
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF2F2F7)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "국민",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF666680),
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "국민은행",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = BlackBrown,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "1234-56-78901",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MySubText,
            )
        }
        Text(
            text = "바꾸기 ›",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = MySubText,
            modifier = Modifier.clickable { /* TODO: 계좌 변경 화면 이동 */ },
        )
    }
}

@Composable
private fun ArrivalInfo() {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFDBF2E3))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "🕒", fontSize = 18.sp)
        Spacer(Modifier.size(12.dp))
        Column {
            Text(
                text = "화요일 7/3 도착 예정",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF26A869),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "국민은행으로 보내요",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = MySubText,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PointWithdrawScreenPreview() {
    OnulDo_FETheme {
        PointWithdrawScreen(onBack = {})
    }
}

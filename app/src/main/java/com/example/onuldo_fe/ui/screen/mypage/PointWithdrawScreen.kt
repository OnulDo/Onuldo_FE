package com.example.onuldo_fe.ui.screen.mypage

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.RefreshOnResume
import com.example.onuldo_fe.ui.screen.mypage.component.AmountChip
import com.example.onuldo_fe.ui.screen.mypage.component.AmountInputBox
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageNoticeBox
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.ui.screen.mypage.component.PointCtaButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.mypage.PointWithdrawViewModel

private data class WithdrawPreset(val label: String, val value: Int)

/**
 * 포인트 출금 — Figma node `5154:3517`.
 * 출금 가능 카드 + 금액 입력/칩 + 보낼 곳(정식출시 안내).
 *
 * 출금은 충전과 달리 칩이 **금액을 지정**한다(가산이 아님). '전액'은 실제 출금 가능액이다.
 * '출금하기'는 `POST /api/users/me/wallet/withdraw`로 출금하고, **성공 응답에서만** [onBack]을 호출한다.
 * (보낼 곳은 계좌/PG 연동 전이라 안내 박스로 대체한다.)
 */
@Composable
fun PointWithdrawScreen(
    onBack: () -> Unit,
    viewModel: PointWithdrawViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    // 화면이 보일 때마다 최신 출금 가능 잔액을 읽는다.
    RefreshOnResume { viewModel.loadBalance() }

    // 출금 실패 안내를 한 번만 토스트로 띄운다.
    val context = LocalContext.current
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onErrorShown()
        }
    }

    // 출금 가능액 = 보유 잔액. 진행 중(pendingPoints)은 표시용이며 여기는 화면
    val withdrawable = state.balance ?: 0L
    // “전액”은 API가 허용하는 최대 출금 가능액 명시적 표기
    val maxWithdrawable = withdrawable.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

    val presets = listOf(
        WithdrawPreset("전액", maxWithdrawable),
        WithdrawPreset("10,000P", 10_000),
        WithdrawPreset("30,000P", 30_000),
        WithdrawPreset("50,000P", 50_000),
    )
    // 0에서 시작해 칩으로 금액을 지정한다(Figma 기본 상태가 0P).
    var selectedPreset by remember { mutableStateOf<Int?>(null) }
    val amount = selectedPreset?.let { presets[it].value } ?: 0
    val amountText = "%,d".format(amount)
    // 1 이상, 출금 가능액 이내, 제출 중이 아닐 때만 출금 활성.
    val canWithdraw = amount in 1..withdrawable.toInt() && !state.isSubmitting
    // 출금 가능액을 넘는 금액을 고르면(예: 잔액보다 큰 프리셋) 빨간 안내를 띄우고 버튼은 계속 비활성.
    val overLimit = amount > 0 && amount.toLong() > withdrawable

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
            AvailableCard(
                withdrawable = withdrawable,
                balance = state.balance ?: 0L,
                pending = state.pendingPoints,
            )

            Spacer(Modifier.height(30.dp))
            Text(
                text = "얼마 출금할까요?",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = BlackBrown,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(16.dp))
            AmountInputBox(
                amount = amountText,
                unit = "P",
                // 칩으로 금액을 고르면 박스 왼쪽에 X가 뜨고, 누르면 선택 해제(0).
                clearable = amount > 0,
                onClear = { selectedPreset = null },
                // 출금 가능액 초과 시 테두리 빨강.
                isError = overLimit,
            )

            // 출금 가능액 초과 안내(서버 요청 전 클라에서 즉시 알림).
            if (overLimit) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "출금 가능 금액 ${"%,d".format(withdrawable)}P를 초과했어요",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Red,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                presets.forEachIndexed { i, preset ->
                    AmountChip(
                        text = preset.label,
                        selected = selectedPreset == i,
                        onClick = { selectedPreset = i },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = "보낼 곳",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = BlackBrown,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(12.dp))
            MyPageNoticeBox(
                iconRes = R.drawable.mypage_coming_soon_icon,
                text = "정식출시 이후 업데이트 예정이에요!",
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .height(186.dp),
            )

            Spacer(Modifier.height(24.dp))
        }

        PointCtaButton(
            text = "출금하기",
            enabled = canWithdraw,
            onClick = { viewModel.withdraw(amount, onSuccess = onBack) },
        )
    }
}

/**
 * 출금 가능 금액 카드 — 흰 배경 + Persimmon 1dp 테두리(라운드 14).
 * 금액과 보유/진행 중은 지갑 요약 API 값이다.
 */
@Composable
private fun AvailableCard(withdrawable: Long, balance: Long, pending: Long) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, Persimmon, RoundedCornerShape(14.dp))
            .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 11.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "출금할 수 있는 금액",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Persimmon,
        )
        Text(
            text = "${"%,d".format(withdrawable)}원",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = BlackBrown,
        )
        Text(
            text = "보유 ${"%,d".format(balance)}P · 진행 중 ${"%,d".format(pending)}P",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = DarkBrown50,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PointWithdrawScreenPreview() {
    OnulDo_FETheme {
        PointWithdrawScreen(onBack = {})
    }
}

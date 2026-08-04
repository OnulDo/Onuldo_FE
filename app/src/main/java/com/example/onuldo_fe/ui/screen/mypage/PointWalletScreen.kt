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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.data.user.dto.PointTransactionTypeDto
import com.example.onuldo_fe.model.user.PointTransaction
import com.example.onuldo_fe.model.user.WalletSummary
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.utils.formatAmount
import com.example.onuldo_fe.utils.formatPoint
import com.example.onuldo_fe.utils.formatSignedAmount
import com.example.onuldo_fe.viewmodel.mypage.PointWalletViewModel
import com.example.onuldo_fe.viewmodel.mypage.WalletFilter
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

// --- 거래 카테고리별 색상 (Figma 실측) ---
private val TxSubText = Color(0xFF80808C)
private val TxDark = Color(0xFF212126)
private val TxBalance = Color(0xFFB2B2BF)

private enum class TxCategory(
    val label: String,
    val iconBg: Color,
    val accent: Color,
    val amountColor: Color,
) {
    환급("환급", Color(0xFFDBF2E3), Color(0xFF26A869), Color(0xFF26A869)),
    충전("충전", Color(0xFFDBEBFF), Color(0xFF217DF2), Color(0xFF217DF2)),
    예치("예치", Color(0xFFFFEBE0), Color(0xFFFF6B36), TxDark),
    차감("차감", Color(0xFFFCE3DE), Color(0xFFD95247), Color(0xFFD95247)),
    출금("출금", Color(0xFFF0EBE3), TxSubText, TxDark),
}

private data class Tx(
    val category: TxCategory,
    val title: String,
    val date: String,
    val amount: String,
    val balance: String,
)

/** 서버 거래 종류 → 화면 배지. 서버에는 "차감" 종류가 없어 [TxCategory.차감]은 매핑되지 않는다. */
private fun PointTransaction.toTx(): Tx {
    val category = when (type) {
        PointTransactionTypeDto.CHARGE -> TxCategory.충전
        PointTransactionTypeDto.WITHDRAW -> TxCategory.출금
        PointTransactionTypeDto.DEPOSIT -> TxCategory.예치
        PointTransactionTypeDto.REFUND -> TxCategory.환급
        null -> TxCategory.예치
    }
    return Tx(
        category = category,
        title = title,
        date = formatTransactionDate(date),
        amount = "${formatSignedAmount(amount)}P",
        balance = "잔액 ${formatPoint(balanceAfter)}",
    )
}

/** `2026-05-19` → `5/19`. 형식이 다르면 원본을 그대로 쓴다. */
private fun formatTransactionDate(raw: String): String {
    val parts = raw.split("-")
    if (parts.size != 3) return raw
    val month = parts[1].toIntOrNull() ?: return raw
    val day = parts[2].toIntOrNull() ?: return raw
    return "$month/$day"
}

/**
 * 포인트 지갑 상세 — Figma node `4019:4132`.
 * 보유 포인트 · 충전/출금 · 누적 정산 · 거래 내역(필터).
 *
 * 요약은 `GET /wallet/summary`, 내역은 `GET /wallet/transactions`(커서 페이징)로 채운다.
 */
@Composable
fun PointWalletScreen(
    onBack: () -> Unit,
    onChargeClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    viewModel: PointWalletViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val visibleTx = state.transactions.map { it.toTx() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        MyPageTopBar(title = "포인트 지갑", onBack = onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 24.dp, bottom = 24.dp,
            ),
        ) {
            item { BalanceCard(state.summary) }
            item { Spacer(Modifier.height(22.dp)) }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    ActionButton("충전하기", filled = true, onClick = onChargeClick, modifier = Modifier.weight(1f))
                    ActionButton("출금하기", filled = false, onClick = onWithdrawClick, modifier = Modifier.weight(1f))
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
            item { SettlementCard(state.summary) }
            item { Spacer(Modifier.height(24.dp)) }
            item {
                Text(
                    text = "거래 내역",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BlackBrown,
                    modifier = Modifier.padding(start = 24.dp),
                )
            }
            item { Spacer(Modifier.height(14.dp)) }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    WalletFilter.entries.forEach { filter ->
                        FilterChip(
                            text = filter.label,
                            selected = state.selectedFilter == filter,
                            onClick = { viewModel.selectFilter(filter) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(18.dp)) }
            items(visibleTx) { tx ->
                TxRow(tx)
                Spacer(Modifier.height(8.dp))
            }
            // 목록 끝에 닿으면 다음 페이지를 이어 받는다(커서 페이징).
            if (state.hasNext) {
                item {
                    LaunchedEffect(state.transactions.size) { viewModel.loadMore() }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(summary: WalletSummary) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFFF4EC))
            .border(1.dp, Color(0xFFFFE3D0), RoundedCornerShape(18.dp))
            .padding(20.dp),
    ) {
        Text(
            text = "보유 포인트",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 0.44.sp,
            color = Color(0xFFC7430B),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${formatAmount(summary.balance)} P",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            letterSpacing = (-0.6).sp,
            color = Persimmon,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "진행 중 예치 ${formatPoint(summary.pendingPoints)}",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = Color(0xFF6E5B49),
        )
    }
}

@Composable
private fun ActionButton(text: String, filled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (filled) Persimmon else White)
            .then(if (filled) Modifier else Modifier.border(1.5.dp, Color(0xFFE5DDD0), RoundedCornerShape(14.dp)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (filled) White else BlackBrown,
        )
    }
}

@Composable
private fun SettlementCard(summary: WalletSummary) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, Color(0xFFF2EADF), RoundedCornerShape(14.dp))
            .padding(vertical = 14.dp, horizontal = 20.dp),
    ) {
        Text(
            text = "누적 정산 내역",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = BlackBrown,
        )
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            SettlementItem("총 예치", formatPoint(summary.totalDeposit), TxDark, Modifier.weight(1f))
            SettlementItem(
                "환급",
                "+${formatPoint(summary.totalRefund)}",
                Color(0xFF18A77A),
                Modifier.weight(1f),
            )
            SettlementItem(
                "차감",
                "−${formatPoint(summary.totalPenalty)}",
                Color(0xFFDC3F3F),
                Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "평균 환급률",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = Color(0xFF6E5B49),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${summary.averageReturnRate}%",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Persimmon,
            )
        }
    }
}

@Composable
private fun SettlementItem(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            color = Color(0xFF6E5B49),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = valueColor,
        )
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) BlackBrown else White)
            .then(if (selected) Modifier else Modifier.border(1.dp, Color(0xFFE5DDD0), RoundedCornerShape(999.dp)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = if (selected) White else Color(0xFF3B2D22),
        )
    }
}

@Composable
private fun TxRow(tx: Tx) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .border(1.dp, Color(0xFFE8DECC), RoundedCornerShape(12.dp))
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tx.category.iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = tx.category.label,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = tx.category.accent,
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.title,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TxDark,
                maxLines = 1,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${tx.category.label} · ${tx.date}",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = TxSubText,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = tx.amount,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = tx.category.amountColor,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = tx.balance,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                color = TxBalance,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 940)
@Composable
private fun PointWalletScreenPreview() {
    OnulDo_FETheme {
        PointWalletScreen(onBack = {}, onChargeClick = {}, onWithdrawClick = {})
    }
}

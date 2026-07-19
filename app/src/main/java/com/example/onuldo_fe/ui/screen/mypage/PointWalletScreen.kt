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
import androidx.compose.runtime.getValue
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
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
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

// 더미 데이터 — TODO: API 연동 시 교체
private val sampleTx = listOf(
    Tx(TxCategory.환급, "독서 30분 챌린지 성공", "5/19", "+18,400P", "잔액 52,000P"),
    Tx(TxCategory.충전, "포인트 충전 · 카카오페이", "5/18", "+30,000P", "잔액 33,600P"),
    Tx(TxCategory.예치, "새벽 6시 기상 챌린지", "5/17", "−10,000P", "잔액 3,600P"),
    Tx(TxCategory.차감, "30분 러닝 챌린지 실패", "5/15", "−5,000P", "잔액 13,600P"),
    Tx(TxCategory.출금, "국민은행 1234-56-78901", "5/10", "−50,000P", "잔액 18,600P"),
)

private val filters = listOf("전체", "충전", "환급", "차감", "출금")

/**
 * 포인트 지갑 상세 — Figma node `4019:4132`.
 * 보유 포인트 · 충전/출금 · 누적 정산 · 거래 내역(필터).
 *
 * 값은 더미. TODO: ViewModel/API 연동.
 */
@Composable
fun PointWalletScreen(
    onBack: () -> Unit,
    onChargeClick: () -> Unit,
    onWithdrawClick: () -> Unit,
) {
    var selectedFilter by remember { mutableStateOf("전체") }
    val visibleTx = if (selectedFilter == "전체") sampleTx
    else sampleTx.filter { it.category.label == selectedFilter }

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
            item { BalanceCard() }
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
            item { SettlementCard() }
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
                    filters.forEach { f ->
                        FilterChip(
                            text = f,
                            selected = selectedFilter == f,
                            onClick = { selectedFilter = f },
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
        }
    }
}

@Composable
private fun BalanceCard() {
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
            text = "52,000 P",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            letterSpacing = (-0.6).sp,
            color = Persimmon,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "진행 중 예치 7,000P",
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
private fun SettlementCard() {
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
            SettlementItem("총 예치", "350,000P", TxDark, Modifier.weight(1f))
            SettlementItem("환급", "+298,000P", Color(0xFF18A77A), Modifier.weight(1f))
            SettlementItem("차감", "−52,000P", Color(0xFFDC3F3F), Modifier.weight(1f))
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
                text = "85%",
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

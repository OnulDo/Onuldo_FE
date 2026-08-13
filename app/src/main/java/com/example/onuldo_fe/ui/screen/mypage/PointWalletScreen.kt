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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.data.user.dto.PointTransactionTypeDto
import com.example.onuldo_fe.model.user.PointTransaction
import com.example.onuldo_fe.model.user.WalletSummary
import com.example.onuldo_fe.ui.component.RefreshOnResume
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.utils.formatAmount
import com.example.onuldo_fe.utils.formatPoint
import com.example.onuldo_fe.utils.formatSignedAmount
import com.example.onuldo_fe.viewmodel.mypage.PointWalletViewModel
import com.example.onuldo_fe.viewmodel.mypage.WalletFilter
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.figmaLineBox
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Persimmon70
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.ui.theme.LocalSpacing

private enum class TxCategory(
    val label: String,
    val iconBg: Color,
    val accent: Color,
    val amountColor: Color,
) {
    // 디자인시스템 컬러 사용. 환급 성공=green, 실패(차감)=red, 그 외(충전·출금·예치)=persimmon
    환급("환급", Green2, Green, Green),
    차감("차감", Red2, Red, Red),
    // 충전만 금액 글씨도 persimmon100. 출금·예치는 금액을 BlackBrown으로 둔다.
    충전("충전", Persimmon10, Persimmon, Persimmon),
    예치("예치", Persimmon10, Persimmon, BlackBrown),
    출금("출금", Persimmon10, Persimmon, BlackBrown),

    /** 예외) 서버가 새 거래 종류를 추가해 앱이 해석하지 못할 때 쓰는 중립 표기 */
    기타("기타", Persimmon10, Persimmon, BlackBrown),
}

private data class Tx(
    val category: TxCategory,
    val title: String,
    val date: String,
    val amount: String,
    val balance: String,
    // 환급(REFUND)에서 "예치금 (구분자) 조정액" 표기. 조정 없을 땐 null → 줄 자체를 안 그린다.
    // 예치금은 회색, 조정액만 색 강조(보너스=초록/차감=빨강)라 AnnotatedString으로 담는다.
    val breakdown: AnnotatedString? = null,
)

/**
 * 서버 거래 종류 → 화면 배지.
 *
 * "차감"은 별도 타입이 아니라 REFUND 안에서 `adjustmentAmount` 부호로! 구분된다
 * (`amount = depositAmount + adjustmentAmount`, 조정액 음수 = 차감/실패)
 * depositAmount·adjustmentAmount는 REFUND에서만 non-null로 (실제 확인 필요,,.)
 */
private fun PointTransaction.toTx(): Tx {
    // REFUND 중 조정액이 음수면 실패(차감/빨강), 그 외엔 성공·순수환급(초록).
    val isPenalty = type == PointTransactionTypeDto.REFUND && (adjustmentAmount ?: 0) < 0
    val category = when (type) {
        PointTransactionTypeDto.CHARGE -> TxCategory.충전
        PointTransactionTypeDto.WITHDRAW -> TxCategory.출금
        PointTransactionTypeDto.DEPOSIT -> TxCategory.예치
        PointTransactionTypeDto.REFUND -> if (isPenalty) TxCategory.차감 else TxCategory.환급
        null -> TxCategory.기타
    }
    // breakdown: REFUND이고 조정액이 있을 때만. 음수는 abs로 크기만 표시하고 구분자를 - 로 쓴다.
    // 예치금(회색) + 조정액(보너스=초록/차감=빨강)만 색 강조.
    val breakdown: AnnotatedString? =
        if (type == PointTransactionTypeDto.REFUND &&
            depositAmount != null && adjustmentAmount != null && adjustmentAmount != 0
        ) {
            val sep = if (adjustmentAmount < 0) "-" else "+"
            // 콤마 없이 표기. 예치금=BlackBrown, 기호=DarkBrown50, 조정액=색강조(보너스 초록/차감 빨강).
            buildAnnotatedString {
                withStyle(SpanStyle(color = BlackBrown)) {
                    append("${depositAmount}P ")
                }
                withStyle(SpanStyle(color = DarkBrown50)) {
                    append("$sep ")
                }
                withStyle(SpanStyle(color = category.amountColor)) {
                    append("${kotlin.math.abs(adjustmentAmount)}P")
                }
            }
        } else {
            null
        }
    return Tx(
        category = category,
        title = title,
        date = formatTransactionDate(date),
        amount = "${formatSignedAmount(amount)}P",
        balance = "잔액 ${formatPoint(balanceAfter)}",
        breakdown = breakdown,
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
 * 포인트 지갑 상세 — Figma node `5652:2948`.
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
    val spacing = LocalSpacing.current
    val state by viewModel.uiState.collectAsState()
    val visibleTx = state.transactions.map { it.toTx() }

    // 충전 화면에서 돌아오면 잔액·내역이 바뀌어 있다. ViewModel은 백스택에 살아 있어
    // 한 번 조회한 값이 그대로 남으므로, 화면이 보일 때마다 새로 읽는다(최초 진입 포함).
    RefreshOnResume { viewModel.load() }

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
            // Figma(5652:2948) 절대좌표 기준 간격: 잔액카드 끝 210 → 버튼 229
            item { Spacer(Modifier.height(19.dp)) }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.spacing20),
                    horizontalArrangement = Arrangement.spacedBy(spacing.spacing14),
                ) {
                    ActionButton("충전하기", filled = true, onClick = onChargeClick, modifier = Modifier.weight(1f))
                    ActionButton("출금하기", filled = false, onClick = onWithdrawClick, modifier = Modifier.weight(1f))
                }
            }
            // 버튼 끝 285 → 누적 정산 카드 305
            item { Spacer(Modifier.height(spacing.spacing20)) }
            item { SettlementCard(state.summary) }
            // 정산 카드 끝 413 → "거래 내역" 437
            item { Spacer(Modifier.height(spacing.spacing24)) }
            item {
                Text(
                    text = "거래 내역",
                    style = OnulDoTypography.body4Bold,
                    color = BlackBrown,
                    modifier = Modifier.padding(start = spacing.spacing20),
                )
            }
            // "거래 내역" 끝 459 → 필터 칩 471
            item { Spacer(Modifier.height(spacing.spacing12)) }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.spacing20),
                    // 칩 x좌표 20 / 90.8 / 161.6 / 232 / 303.2, 폭 66 → 간격 4.8
                    horizontalArrangement = Arrangement.spacedBy(4.8.dp),
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
            // 필터 칩 끝 503 → 거래 내역 목록 519
            item { Spacer(Modifier.height(spacing.spacing16)) }
            items(visibleTx) { tx ->
                TxRow(tx)
                Spacer(Modifier.height(spacing.spacing8))
            }
            // 목록 끝에 닿으면 다음 페이지를 이어 받는다(커서 페이징).
            if (state.hasNext) {
                item {
                    LaunchedEffect(state.transactions.size) { viewModel.loadMore() }
                    Spacer(Modifier.height(spacing.spacing8))
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(summary: WalletSummary) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .padding(horizontal = spacing.spacing20)
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Persimmon10)
            .border(1.dp, Persimmon20, RoundedCornerShape(14.dp))
            .padding(start = spacing.spacing20),
        verticalArrangement = Arrangement.spacedBy(spacing.spacing10, Alignment.CenterVertically),
    ) {
        Text(
            text = "보유 포인트",
            style = OnulDoTypography.caption3Bold,
            color = DarkBrown70,
        )
        Text(
            text = "${formatAmount(summary.balance)}P",
            // 잔액 금액 30sp는 디자인시스템에 이름 붙은 스타일이 없는 1회성 크기다(Figma 실측).
            // Figma가 leading-[normal]이라 displayBold의 행간 40을 쓰지 않고 30sp 기준값으로 낮춘다.
            style = OnulDoTypography.displayBold.copy(fontSize = 30.sp, lineHeight = 36.sp),
            color = Persimmon,
        )
        Text(
            text = "진행 중 예치 ${formatPoint(summary.pendingPoints)}",
            style = OnulDoTypography.caption3Regular,
            color = DarkBrown70,
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
            .then(if (filled) Modifier else Modifier.border(1.5.dp, DarkBrown40, RoundedCornerShape(14.dp)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = OnulDoTypography.body4Bold,
            // 채운 버튼의 글자는 White가 아니라 배경색과 같은 SourCream이다(Figma).
            color = if (filled) SourCream else BlackBrown,
        )
    }
}

@Composable
private fun SettlementCard(summary: WalletSummary) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .padding(horizontal = spacing.spacing20)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
            // Figma 실측: 위 14 / 아래 11 (카드 높이 108)
            .padding(top = spacing.spacing14, bottom = 11.dp)
            .padding(horizontal = spacing.spacing20),
    ) {
        // Figma(5652:2948): 평균 환급률은 카드 하단이 아니라 제목 오른쪽에 붙는다.
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "누적 정산 내역",
                style = OnulDoTypography.body4Bold.figmaLineBox(),
                color = BlackBrown,
            )
            Spacer(Modifier.width(spacing.spacing10))
            Text(
                text = "평균 환급률",
                style = OnulDoTypography.caption2Regular.figmaLineBox(),
                color = DarkBrown70,
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${summary.averageReturnRate}%",
                style = OnulDoTypography.caption2Bold.figmaLineBox(),
                color = Persimmon70,
            )
        }
        Spacer(Modifier.height(spacing.spacing20))
        Row(modifier = Modifier.fillMaxWidth()) {
            SettlementItem("총 예치", formatPoint(summary.totalDeposit), BlackBrown, Modifier.weight(1f))
            SettlementItem(
                "환급",
                "+${formatPoint(summary.totalRefund)}",
                Green,
                Modifier.weight(1f),
            )
            SettlementItem(
                "차감",
                "−${formatPoint(summary.totalPenalty)}",
                Red,
                Modifier.weight(1f),
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
            style = OnulDoTypography.caption2Regular.figmaLineBox(),
            color = DarkBrown70
        )
        // Figma 라벨 top 361 → 값 top 380. 라벨 lineHeight가 20이라 별도 간격이 없다.
        Text(
            text = value,
            style = OnulDoTypography.caption2Bold.figmaLineBox(),
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
            // Figma(5652:2948): 선택 칩은 검정이 아니라 Persimmon 채움.
            .background(if (selected) Persimmon else White)
            .then(if (selected) Modifier else Modifier.border(1.dp, DarkBrown40, RoundedCornerShape(999.dp)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = OnulDoTypography.caption2Bold,
            color = if (selected) White else BlackBrown,
        )
    }
}

@Composable
private fun TxRow(tx: Tx) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .padding(horizontal = spacing.spacing20)
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
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
                style = OnulDoTypography.caption1Bold,
                color = tx.category.accent,
            )
        }
        Spacer(Modifier.size(spacing.spacing12))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.title,
                style = OnulDoTypography.body4Bold,
                color = BlackBrown,
                maxLines = 1,
            )
            // Figma: 제목 top 17 → 날짜 top 39. 제목 lineHeight 22라 별도 간격이 없다.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${tx.category.label} · ${tx.date}",
                    style = OnulDoTypography.caption3Medium,
                    color = DarkBrown40,
                )
                // 환급 breakdown("예치금 ± 조정액")은 값이 있을 때만, 날짜 오른쪽에 붙는다(Figma).
                if (tx.breakdown != null) {
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = tx.breakdown,
                        style = OnulDoTypography.caption2Bold,
                    )
                }
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = tx.amount,
                style = OnulDoTypography.body4Bold,
                color = tx.category.amountColor,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = tx.balance,
                style = OnulDoTypography.caption4Regular,
                color = DarkBrown40,
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

package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.AuthErrorBanner
import com.example.onuldo_fe.ui.screen.mypage.component.AmountChip
import com.example.onuldo_fe.ui.screen.mypage.component.AmountInputBox
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageNoticeBox
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.ui.screen.mypage.component.PointCtaButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.utils.formatPoint
import com.example.onuldo_fe.viewmodel.mypage.PointChargeViewModel

private data class AmountPreset(val label: String, val value: Int)

private val chargePresets = listOf(
    AmountPreset("+10,000", 10_000),
    AmountPreset("+30,000", 30_000),
    AmountPreset("+50,000", 50_000),
    AmountPreset("+100,000", 100_000),
)

@Composable
fun PointChargeScreen(
    onBack: () -> Unit,
    viewModel: PointChargeViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    // 칩은 누를 때마다 금액을 더한다(Figma 5154:3439). 0에서 시작해 사용자가 쌓아 올린다.
    var amount by remember { mutableIntStateOf(0) }

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
            Spacer(Modifier.height(56.dp))
            Text(
                text = "얼마 충전할까요?",
                style = OnulDoTypography.headline3Bold,
                color = BlackBrown,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "보유 ${formatPoint(state.balance)}",
                style = OnulDoTypography.caption1Medium,
                color = MySubText,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Spacer(Modifier.height(18.dp))
            AmountInputBox(
                amount = amountText,
                unit = "P",
                // 칩으로 금액이 쌓이면 박스 왼쪽에 X가 뜨고, 누르면 0으로.
                clearable = amount > 0,
                onClear = { amount = 0 },
            )

            Spacer(Modifier.height(22.dp))
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

            Spacer(Modifier.height(20.dp))
            Text(
                text = "결제할 방법",
                style = OnulDoTypography.body2Bold,
                color = BlackBrown,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(16.dp))
            MyPageNoticeBox(
                iconRes = R.drawable.mypage_coming_soon_icon,
                text = "정식출시 이후 업데이트 예정이에요!",
                // 충전은 350×250로 키운다(출금은 186). 내용은 세로 중앙 정렬.
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .height(250.dp),
            )

            state.errorMessage?.let { message ->
                Spacer(Modifier.height(12.dp))
                AuthErrorBanner(text = message, modifier = Modifier.padding(horizontal = 20.dp))
            }

            // 안내 박스 아래로 90dp 띄운다.
            Spacer(Modifier.height(90.dp))
        }

        PointCtaButton(
            text = if (state.isLoading) "충전 중..." else "충전하기",
            // 금액을 하나도 고르지 않으면 충전할 수 없다.
            enabled = amount > 0 && !state.isLoading,
            onClick = { viewModel.charge(amount, onBack) },
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PointChargeScreenPreview() {
    OnulDo_FETheme {
        PointChargeScreen(onBack = {})
    }
}

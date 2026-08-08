package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.screen.challenge.participate.component.InsufficientPointDialog
import com.example.onuldo_fe.ui.screen.party.components.PartyCapacitySelector
import com.example.onuldo_fe.ui.screen.party.components.PartyChallengeSelector
import com.example.onuldo_fe.ui.screen.party.components.PartyNameTextField
import com.example.onuldo_fe.ui.screen.party.components.PartyOptionSelector
import com.example.onuldo_fe.ui.screen.party.components.PartyTopBar
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge
import com.example.onuldo_fe.ui.theme.*
import java.text.Normalizer

@Composable
fun PartyCreateScreen(
    partyName: String,
    onPartyNameChange: (String) -> Unit,
    capacity: Int,
    onCapacityChange: (Int) -> Unit,
    selectedChallenge: Challenge?,
    onChallengeClick: () -> Unit,
    onBack: () -> Unit,
    onCreate: (period: String, deposit: Int) -> Unit,
    availablePoint: Int? = 50_000,
    onChargePoint: () -> Unit = {},
    isSubmitting: Boolean = false,
    errorMessage: String? = null,
    showPointShortageFromServer: Boolean = false,
    onPointShortageDismiss: () -> Unit = {},
    selectedChallengeCategoryLabel: String? = null,
    checkPointBeforeRequest: Boolean = false
) {
    val spacing = LocalSpacing.current
    val periods = listOf("2주", "4주", "8주", "12주")
    val deposits = listOf(10_000, 20_000, 30_000, 50_000)
    var selectedPeriod by remember(selectedChallenge?.id) { mutableIntStateOf(-1) }
    var selectedDeposit by remember(selectedChallenge?.id) { mutableIntStateOf(-1) }
    var showPointDialog by remember { mutableStateOf(false) }
    var isPartyNameError by remember { mutableStateOf(false) }

    LaunchedEffect(showPointShortageFromServer) {
        if (showPointShortageFromServer) showPointDialog = true
    }
    // 단어 사이 공백은 허용하고, 앞뒤 공백은 아래 정규화 과정에서 제거한다.
    val partyNamePattern = remember { Regex("^[가-힣A-Za-z0-9 ]{2,20}$") }
    val normalizedPartyName = remember(partyName) {
        // 한글 입력기에서 조합형 자모로 전달된 이름을 완성형 한글로 변환
        Normalizer.normalize(partyName.trim(), Normalizer.Form.NFC)
    }
    val enabled = normalizedPartyName.isNotBlank() &&
        selectedChallenge != null &&
        selectedPeriod >= 0 &&
        selectedDeposit >= 0

    Column(Modifier.fillMaxSize().background(SourCream)) {
        PartyTopBar(title = "파티 만들기", onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.spacing20)
        ) {
            // TODO 디자인 시스템에 53dp 토큰이 추가되면 LocalSpacing으로 교체
            Spacer(Modifier.height(53.dp))
            SectionTitle("파티 이름", 12)
            // TODO 디자인 시스템에 7dp 토큰이 추가되면 LocalSpacing으로 교체
            Spacer(Modifier.height(7.dp))
            PartyNameTextField(
                value = partyName,
                onValueChange = {
                    isPartyNameError = false
                    onPartyNameChange(it)
                },
                isError = isPartyNameError
            )
            if (isPartyNameError) {
                Text(
                    "한글, 영문, 숫자, 공백을 포함해 2~20자로 입력해주세요.",
                    // TODO 디자인 시스템에 4dp·6dp 토큰이 추가되면 LocalSpacing으로 교체
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp),
                    color = Persimmon,
                    fontFamily = Pretendard,
                    fontSize = 11.sp
                )
            }
            Spacer(Modifier.height(spacing.spacing26))
            SectionTitle("함께할 챌린지", 12)
            Spacer(Modifier.height(spacing.spacing8))
            PartyChallengeSelector(
                challenge = selectedChallenge,
                categoryLabel = selectedChallengeCategoryLabel,
                onClick = onChallengeClick
            )
            if (selectedChallenge != null) {
                // TODO 디자인 시스템에 22dp 토큰이 추가되면 LocalSpacing으로 교체
                Spacer(Modifier.height(22.dp))
                SectionTitle("진행 기간", 14)
                // TODO 디자인 시스템에 7dp 토큰이 추가되면 LocalSpacing으로 교체
                Spacer(Modifier.height(7.dp))
                PartyOptionSelector(periods, selectedPeriod, onSelect = { selectedPeriod = it }, textSize = 14.sp)
                // TODO 디자인 시스템에 23dp 토큰이 추가되면 LocalSpacing으로 교체
                Spacer(Modifier.height(23.dp))
                SectionTitle("도전금", 14)
                // TODO 디자인 시스템에 7dp 토큰이 추가되면 LocalSpacing으로 교체
                Spacer(Modifier.height(7.dp))
                PartyOptionSelector(deposits.map { "%,dP".format(it) }, selectedDeposit, onSelect = { selectedDeposit = it }, textSize = 12.sp)
            }
            // TODO 디자인 시스템에 22dp 토큰이 추가되면 LocalSpacing으로 교체
            Spacer(Modifier.height(if (selectedChallenge == null) spacing.spacing26 else 22.dp))
            SectionTitle("모집 인원 (2~5명)", 12)
            Spacer(Modifier.height(spacing.spacing8))
            PartyCapacitySelector(capacity = capacity, onCapacityChange = onCapacityChange)
            Spacer(Modifier.height(spacing.spacing16))
        }
        Box(Modifier.fillMaxWidth().height(138.dp).background(SourCream), contentAlignment = Alignment.TopCenter) {
            errorMessage?.let {
                Text(
                    text = it,
                    // TODO 디자인 시스템에 14dp 토큰이 추가되면 LocalSpacing으로 교체
                    modifier = Modifier.padding(top = 14.dp),
                    color = Persimmon,
                    fontFamily = Pretendard,
                    fontSize = 11.sp
                )
            }
            OnulDoButton(
                text = if (isSubmitting) "만드는 중..." else "파티 만들기",
                onClick = {
                    if (!partyNamePattern.matches(normalizedPartyName)) {
                        isPartyNameError = true
                    } else {
                        onPartyNameChange(normalizedPartyName)
                        val requiredDeposit = deposits[selectedDeposit]
                        // Fake API에서는 화면에 설정된 테스트 포인트로 검증한다.
                        // Real API는 ViewModel이 요청 직전에 최신 지갑 잔액을 다시 조회한다.
                        if (checkPointBeforeRequest && availablePoint != null && availablePoint < requiredDeposit) {
                            showPointDialog = true
                        } else {
                            onCreate(periods[selectedPeriod], requiredDeposit)
                        }
                    }
                },
                enabled = enabled && !isSubmitting,
                // TODO 디자인 시스템에 40dp 토큰이 추가되면 LocalSpacing으로 교체
                modifier = Modifier.padding(top = 40.dp),
                height = 52.dp,
                fontSize = 17.sp,
                lineHeight = 20.sp
            )
        }
    }

    if (showPointDialog) {
        InsufficientPointDialog(
            ownedPoint = availablePoint?.toLong(),
            requiredPoint = deposits[selectedDeposit],
            onDismiss = {
                showPointDialog = false
                onPointShortageDismiss()
            },
            onCharge = {
                showPointDialog = false
                onPointShortageDismiss()
                onChargePoint()
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String, size: Int) = Text(
    text = text,
    // TODO 디자인 시스템에 4dp 토큰이 추가되면 LocalSpacing으로 교체
    modifier = Modifier.padding(start = 4.dp),
    color = BlackBrown,
    fontFamily = Pretendard,
    fontSize = size.sp,
    lineHeight = 22.sp,
    fontWeight = FontWeight.Bold
)

@Preview(name = "파티 생성 - 챌린지 미선택", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyCreateEmptyPreview() { OnulDo_FETheme { PartyCreateScreen("", {}, 5, {}, null, {}, {}, { _, _ -> }) } }

@Preview(name = "파티 생성 - 챌린지 선택", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyCreateSelectedPreview() { OnulDo_FETheme { PartyCreateScreen("갓생팟", {}, 5, {}, Challenge(1L, "30일 헬스 챌린지", 0), {}, {}, { _, _ -> }) } }

package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.screen.party.component.PartyCapacitySelector
import com.example.onuldo_fe.ui.screen.party.component.PartyChallengeSelector
import com.example.onuldo_fe.ui.screen.party.component.PartyNameTextField
import com.example.onuldo_fe.ui.screen.party.component.PartyOptionSelector
import com.example.onuldo_fe.ui.screen.party.component.PartyInsufficientPointDialog
import com.example.onuldo_fe.ui.theme.*

@Composable
fun PartyCreateScreen(
    partyName: String,
    onPartyNameChange: (String) -> Unit,
    capacity: Int,
    onCapacityChange: (Int) -> Unit,
    selectedChallenge: PartyChallengeUi?,
    onChallengeClick: () -> Unit,
    onBack: () -> Unit,
    onCreate: (period: String, deposit: Int) -> Unit,
    availablePoint: Int = 50_000,
    onChargePoint: () -> Unit = {},
    isSubmitting: Boolean = false,
    errorMessage: String? = null
) {
    val periods = listOf("2주", "4주", "8주", "12주")
    val deposits = listOf(10_000, 20_000, 30_000, 50_000)
    var selectedPeriod by remember(selectedChallenge?.id) { mutableIntStateOf(-1) }
    var selectedDeposit by remember(selectedChallenge?.id) { mutableIntStateOf(-1) }
    var showPointDialog by remember { mutableStateOf(false) }
    var isPartyNameError by remember { mutableStateOf(false) }
    val partyNamePattern = remember { Regex("^[가-힣A-Za-z0-9]{2,20}$") }
    val enabled = partyName.isNotBlank() &&
        selectedChallenge != null &&
        selectedPeriod >= 0 &&
        selectedDeposit >= 0

    Column(Modifier.fillMaxSize().background(SourCream).systemBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
            OnulDoBackButton(Modifier.padding(start = 20.dp), onClick = onBack)
            Text("파티 만들기", Modifier.padding(start = 14.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(47.dp))
            SectionTitle("파티 이름", 13)
            Spacer(Modifier.height(10.dp))
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
                    "한글, 영문, 숫자 2~20자로 입력해주세요.",
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp),
                    color = Persimmon,
                    fontFamily = Pretendard,
                    fontSize = 11.sp
                )
            }
            Spacer(Modifier.height(19.dp))
            SectionTitle("함께할 챌린지", 13)
            Spacer(Modifier.height(10.dp))
            PartyChallengeSelector(challenge = selectedChallenge, onClick = onChallengeClick)
            if (selectedChallenge != null) {
                Spacer(Modifier.height(19.dp))
                SectionTitle("진행 기간", 14)
                Spacer(Modifier.height(10.dp))
                PartyOptionSelector(periods, selectedPeriod, onSelect = { selectedPeriod = it }, textSize = 14.sp)
                Spacer(Modifier.height(19.dp))
                SectionTitle("도전금", 14)
                Spacer(Modifier.height(10.dp))
                PartyOptionSelector(deposits.map { "%,dP".format(it) }, selectedDeposit, onSelect = { selectedDeposit = it }, textSize = 12.sp)
            }
            Spacer(Modifier.height(if (selectedChallenge == null) 27.dp else 19.dp))
            SectionTitle("모집 인원 (2~5명)", 13)
            Spacer(Modifier.height(10.dp))
            PartyCapacitySelector(capacity = capacity, onCapacityChange = onCapacityChange)
        }
        Box(Modifier.fillMaxWidth().height(138.dp).background(SourCream), contentAlignment = Alignment.TopCenter) {
            errorMessage?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(top = 14.dp),
                    color = Persimmon,
                    fontFamily = Pretendard,
                    fontSize = 11.sp
                )
            }
            Button(
                onClick = {
                    if (!partyNamePattern.matches(partyName)) {
                        isPartyNameError = true
                    } else {
                        val requiredDeposit = deposits[selectedDeposit]
                        // TODO 파티 생성 API 연동 시 파티장 보유 포인트 검증 성공 후 파티 생성 요청
                        if (availablePoint < requiredDeposit) {
                            showPointDialog = true
                        } else {
                            onCreate(periods[selectedPeriod], requiredDeposit)
                        }
                    }
                },
                enabled = enabled && !isSubmitting,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 40.dp).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Persimmon, contentColor = SourCream, disabledContainerColor = DarkBrown10, disabledContentColor = DarkBrown)
            ) {
                Text(if (isSubmitting) "만드는 중..." else "파티 만들기", fontFamily = Pretendard, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showPointDialog) {
        PartyInsufficientPointDialog(
            ownedPoint = availablePoint,
            requiredPoint = deposits[selectedDeposit],
            onDismiss = { showPointDialog = false },
            onChargeClick = {
                showPointDialog = false
                onChargePoint()
            }
        )
    }
}

@Composable private fun SectionTitle(text: String, size: Int) = Text(text, color = BlackBrown, fontFamily = Pretendard, fontSize = size.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.13).sp)

@Preview(name = "파티 생성 - 챌린지 미선택", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyCreateEmptyPreview() { OnulDo_FETheme { PartyCreateScreen("", {}, 5, {}, null, {}, {}, { _, _ -> }) } }

@Preview(name = "파티 생성 - 챌린지 선택", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyCreateSelectedPreview() { OnulDo_FETheme { PartyCreateScreen("갓생팟", {}, 5, {}, PartyChallengeUi("preview", "30일 헬스 챌린지", "피트니스"), {}, {}, { _, _ -> }) } }

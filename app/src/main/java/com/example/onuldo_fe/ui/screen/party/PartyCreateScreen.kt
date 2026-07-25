package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.onuldo_fe.ui.component.party.PartyCapacitySelector
import com.example.onuldo_fe.ui.component.party.PartyChallengeSelector
import com.example.onuldo_fe.ui.component.party.PartyInsufficientPointDialog
import com.example.onuldo_fe.ui.component.party.PartyNameTextField
import com.example.onuldo_fe.ui.component.party.PartyOptionSelector
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
    availablePoint: Int = 50_000,
    onChargePoint: () -> Unit = {},
    isSubmitting: Boolean = false,
    errorMessage: String? = null,
    // TODO 챌린지 목록 API 연동 시 선택한 챌린지의 category Enum을 화면 표시명으로 변환해 전달
    selectedChallengeCategoryLabel: String? = "생활루틴"
) {
    val periods = listOf("2주", "4주", "8주", "12주")
    val deposits = listOf(10_000, 20_000, 30_000, 50_000)
    var selectedPeriod by remember(selectedChallenge?.id) { mutableIntStateOf(-1) }
    var selectedDeposit by remember(selectedChallenge?.id) { mutableIntStateOf(-1) }
    var showPointDialog by remember { mutableStateOf(false) }
    var isPartyNameError by remember { mutableStateOf(false) }
    val partyNamePattern = remember { Regex("^[가-힣A-Za-z0-9]{2,20}$") }
    val normalizedPartyName = remember(partyName) {
        // 한글 입력기에서 조합형 자모로 전달된 이름을 완성형 한글로 변환
        Normalizer.normalize(partyName.trim(), Normalizer.Form.NFC)
    }
    val enabled = normalizedPartyName.isNotBlank() &&
        selectedChallenge != null &&
        selectedPeriod >= 0 &&
        selectedDeposit >= 0

    Column(Modifier.fillMaxSize().background(SourCream)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(HeaderCream)
        ) {
            OnulDoBackButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = onBack
            )
            Text(
                "파티 만들기",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 48.dp),
                color = BlackBrown,
                fontFamily = Pretendard,
                fontSize = 17.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(47.dp))
            SectionTitle("파티 이름", 12)
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
                    "한글, 영문, 숫자 2~20자로 입력해주세요.",
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp),
                    color = Persimmon,
                    fontFamily = Pretendard,
                    fontSize = 11.sp
                )
            }
            Spacer(Modifier.height(16.dp))
            SectionTitle("함께할 챌린지", 12)
            Spacer(Modifier.height(8.dp))
            PartyChallengeSelector(
                challenge = selectedChallenge,
                categoryLabel = selectedChallengeCategoryLabel,
                onClick = onChallengeClick
            )
            if (selectedChallenge != null) {
                Spacer(Modifier.height(19.dp))
                SectionTitle("진행 기간", 14)
                Spacer(Modifier.height(5.dp))
                PartyOptionSelector(periods, selectedPeriod, onSelect = { selectedPeriod = it }, textSize = 14.sp)
                Spacer(Modifier.height(20.dp))
                SectionTitle("도전금", 14)
                Spacer(Modifier.height(5.dp))
                PartyOptionSelector(deposits.map { "%,dP".format(it) }, selectedDeposit, onSelect = { selectedDeposit = it }, textSize = 12.sp)
            }
            Spacer(Modifier.height(if (selectedChallenge == null) 26.dp else 19.dp))
            SectionTitle("모집 인원 (2~5명)", 12)
            Spacer(Modifier.height(5.dp))
            PartyCapacitySelector(capacity = capacity, onCapacityChange = onCapacityChange)
            Spacer(Modifier.height(16.dp))
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
                    if (!partyNamePattern.matches(normalizedPartyName)) {
                        isPartyNameError = true
                    } else {
                        onPartyNameChange(normalizedPartyName)
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Persimmon,
                    contentColor = SourCream,
                    disabledContainerColor = BlackBrown.copy(alpha = 0.1f),
                    disabledContentColor = BlackBrown.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    if (isSubmitting) "만드는 중..." else "파티 만들기",
                    fontFamily = Pretendard,
                    fontSize = 17.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold
                )
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

@Composable
private fun SectionTitle(text: String, size: Int) = Text(
    text = text,
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
@Composable private fun PartyCreateSelectedPreview() { OnulDo_FETheme { PartyCreateScreen("갓생팟", {}, 5, {}, Challenge(1, "30일 헬스 챌린지", 0), {}, {}, { _, _ -> }) } }

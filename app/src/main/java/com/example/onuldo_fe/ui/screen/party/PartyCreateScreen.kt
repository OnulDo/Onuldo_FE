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
    onCreate: () -> Unit
) {
    val periods = listOf("2주", "4주", "8주", "12주")
    val deposits = listOf(10_000, 20_000, 30_000, 50_000)
    var selectedPeriod by remember(selectedChallenge?.id) { mutableIntStateOf(-1) }
    var selectedDeposit by remember(selectedChallenge?.id) { mutableIntStateOf(-1) }
    val enabled = partyName.length in 2..20 &&
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
                onValueChange = onPartyNameChange
            )
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
            Button(onClick = onCreate, enabled = enabled, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 40.dp).height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Persimmon, contentColor = SourCream, disabledContainerColor = DarkBrown10, disabledContentColor = DarkBrown)) {
                Text("파티 만들기", fontFamily = Pretendard, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable private fun SectionTitle(text: String, size: Int) = Text(text, color = BlackBrown, fontFamily = Pretendard, fontSize = size.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.13).sp)

@Preview(name = "파티 생성 - 챌린지 미선택", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyCreateEmptyPreview() { OnulDo_FETheme { PartyCreateScreen("", {}, 4, {}, null, {}, {}, {}) } }

@Preview(name = "파티 생성 - 챌린지 선택", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyCreateSelectedPreview() { OnulDo_FETheme { PartyCreateScreen("갓생팟", {}, 4, {}, PartyChallengeUi("preview", "30일 헬스 챌린지", "4주", 10_000), {}, {}, {}) } }

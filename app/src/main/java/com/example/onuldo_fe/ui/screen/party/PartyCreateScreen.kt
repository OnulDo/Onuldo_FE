package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
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
    var selectedPeriod by remember(selectedChallenge?.id) { mutableIntStateOf(periods.indexOf(selectedChallenge?.period).takeIf { it >= 0 } ?: 1) }
    var selectedDeposit by remember(selectedChallenge?.id) { mutableIntStateOf(deposits.indexOf(selectedChallenge?.deposit).takeIf { it >= 0 } ?: 0) }
    val enabled = partyName.length in 2..20 && selectedChallenge != null

    Column(Modifier.fillMaxSize().background(SourCream).systemBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
            OnulDoBackButton(Modifier.padding(start = 20.dp), onClick = onBack)
            Text("파티 만들기", Modifier.padding(start = 14.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(47.dp))
            SectionTitle("파티 이름", 13)
            Spacer(Modifier.height(10.dp))
            BasicTextField(
                value = partyName,
                onValueChange = { value -> if (value.length <= 20 && value.all { it.isLetterOrDigit() || it in '가'..'힣' || it == ' ' }) onPartyNameChange(value) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = BlackBrown, fontFamily = Pretendard, fontSize = 13.sp),
                cursorBrush = SolidColor(Persimmon),
                decorationBox = { inner ->
                    Box(Modifier.fillMaxWidth().height(56.dp).background(White, RoundedCornerShape(14.dp)).border(1.dp, DarkBrown40, RoundedCornerShape(14.dp)).padding(horizontal = 19.dp), contentAlignment = Alignment.CenterStart) {
                        if (partyName.isEmpty()) Text("예: 갓생팟", color = DarkBrown40, fontFamily = Pretendard, fontSize = 13.sp)
                        inner()
                    }
                }
            )
            Spacer(Modifier.height(19.dp))
            SectionTitle("함께할 챌린지", 13)
            Spacer(Modifier.height(10.dp))
            if (selectedChallenge == null) {
                Box(Modifier.fillMaxWidth().height(64.dp).dashedBorder(Persimmon).clickable(onClick = onChallengeClick), contentAlignment = Alignment.Center) {
                    Text("＋ 챌린지 선택하기", color = Persimmon, fontFamily = Pretendard, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                SelectedChallengeCard(selectedChallenge.title, onChallengeClick)
                Spacer(Modifier.height(19.dp))
                SectionTitle("진행 기간", 14)
                Spacer(Modifier.height(10.dp))
                PartyOptionRow(periods, selectedPeriod) { selectedPeriod = it }
                Spacer(Modifier.height(19.dp))
                SectionTitle("도전금", 14)
                Spacer(Modifier.height(10.dp))
                PartyOptionRow(deposits.map { "%,dP".format(it) }, selectedDeposit) { selectedDeposit = it }
            }
            Spacer(Modifier.height(if (selectedChallenge == null) 27.dp else 19.dp))
            SectionTitle("모집 인원 (2~5명)", 13)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth().height(60.dp).background(White, RoundedCornerShape(14.dp)).border(1.dp, DarkBrown40, RoundedCornerShape(14.dp)).padding(horizontal = 19.dp), verticalAlignment = Alignment.CenterVertically) {
                CapacityControlIcon(isPlus = false, enabled = capacity > 2) { onCapacityChange(capacity - 1) }
                Text("$capacity 명", Modifier.weight(1f), color = BlackBrown, fontFamily = Pretendard, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                CapacityControlIcon(isPlus = true, enabled = capacity < 5) { onCapacityChange(capacity + 1) }
            }
        }
        Box(Modifier.fillMaxWidth().height(138.dp).background(SourCream), contentAlignment = Alignment.TopCenter) {
            Button(onClick = onCreate, enabled = enabled, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 40.dp).height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Persimmon, contentColor = SourCream, disabledContainerColor = DarkBrown10, disabledContentColor = DarkBrown)) {
                Text("파티 만들기", fontFamily = Pretendard, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable private fun SelectedChallengeCard(title: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(64.dp).background(White, RoundedCornerShape(14.dp)).border(1.5.dp, Persimmon20, RoundedCornerShape(14.dp)).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(56.dp).height(24.dp).background(Persimmon10, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Text("피트니스", color = Persimmon, fontFamily = Pretendard, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
        Text(title, Modifier.weight(1f).padding(start = 8.dp), color = Persimmon, fontFamily = Pretendard, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
            Text("변경", color = DarkBrown50, fontFamily = Pretendard, fontSize = 13.sp)
            Text("›", Modifier.padding(start = 7.dp), color = DarkBrown50, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable private fun CapacityControlIcon(isPlus: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val color = if (isPlus) Persimmon else DarkBrown70
    Canvas(Modifier.size(25.dp).clickable(enabled = enabled, onClick = onClick)) {
        val alpha = if (enabled) 1f else .25f
        drawCircle(color.copy(alpha = alpha), style = Stroke(width = 2.dp.toPx()))
        drawLine(color.copy(alpha = alpha), start = androidx.compose.ui.geometry.Offset(size.width * .29f, size.height * .5f), end = androidx.compose.ui.geometry.Offset(size.width * .71f, size.height * .5f), strokeWidth = 2.dp.toPx())
        if (isPlus) drawLine(color.copy(alpha = alpha), start = androidx.compose.ui.geometry.Offset(size.width * .5f, size.height * .29f), end = androidx.compose.ui.geometry.Offset(size.width * .5f, size.height * .71f), strokeWidth = 2.dp.toPx())
    }
}

@Composable private fun PartyOptionRow(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEachIndexed { index, text ->
            val selected = index == selectedIndex
            Box(Modifier.weight(1f).height(40.dp).background(if (selected) Persimmon else White, RoundedCornerShape(14.dp)).then(if (!selected) Modifier.border(1.dp, DarkBrown40, RoundedCornerShape(14.dp)) else Modifier).clickable { onSelect(index) }, contentAlignment = Alignment.Center) {
                Text(text, color = if (selected) SourCream else BlackBrown, fontFamily = Pretendard, fontSize = if (text.endsWith("P")) 12.sp else 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable private fun SectionTitle(text: String, size: Int) = Text(text, color = BlackBrown, fontFamily = Pretendard, fontSize = size.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.13).sp)

private fun Modifier.dashedBorder(color: Color): Modifier = drawBehind { drawRoundRect(color = color.copy(alpha = .7f), style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()))), cornerRadius = CornerRadius(14.dp.toPx())) }

@Preview(name = "파티 생성 - 챌린지 미선택", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyCreateEmptyPreview() { OnulDo_FETheme { PartyCreateScreen("", {}, 4, {}, null, {}, {}, {}) } }

@Preview(name = "파티 생성 - 챌린지 선택", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyCreateSelectedPreview() { OnulDo_FETheme { PartyCreateScreen("갓생팟", {}, 4, {}, PartyChallengeUi("preview", "30일 헬스 챌린지", "4주", 10_000), {}, {}, {}) } }

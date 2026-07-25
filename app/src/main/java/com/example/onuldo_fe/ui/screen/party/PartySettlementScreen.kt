package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

data class PartySettlementMemberUi(val name: String, val bonus: String)

private val sampleSettlementMembers = listOf(
    PartySettlementMemberUi("민지", "+5,000P"),
    PartySettlementMemberUi("서연", "+5,000P"),
    PartySettlementMemberUi("준호", "+5,000P")
)

@Composable
fun PartySettlementScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit = onBack,
    members: List<PartySettlementMemberUi> = sampleSettlementMembers
) {
    Column(modifier.fillMaxSize().background(SourCream)) {
        Box(Modifier.fillMaxWidth().height(56.dp)) {
            OnulDoBackButton(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp),
                onClick = onBack
            )
            Text(
                "파티 정산 결과",
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 44.dp),
                color = BlackBrown,
                fontFamily = Pretendard,
                fontSize = 17.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Spacer(Modifier.height(47.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier.size(135.dp).background(Persimmon.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        PartySettlementSuccessCharacter()
                    }
                }
                Spacer(Modifier.height(7.dp))
                Text(
                    "전원 성공!",
                    modifier = Modifier.fillMaxWidth(),
                    color = BlackBrown,
                    fontFamily = Pretendard,
                    fontSize = 22.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    "파티 전원이 챌린지를 완주했어요",
                    modifier = Modifier.fillMaxWidth(),
                    color = com.example.onuldo_fe.ui.theme.DarkBrown,
                    fontFamily = Pretendard,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(47.dp))
                Text("내 정산 결과", modifier = Modifier.padding(start = 24.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                SettlementSummaryCard(Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(12.dp))
                Text("파티원 결과", modifier = Modifier.padding(start = 24.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
            }

            items(members, key = { it.name }) { member ->
                PartySettlementMemberCard(
                    member = member,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        Box(Modifier.fillMaxWidth().height(138.dp).background(SourCream), contentAlignment = Alignment.TopCenter) {
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 40.dp).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Persimmon, contentColor = SourCream)
            ) {
                Text("확인", fontFamily = Pretendard, fontSize = 17.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PartySettlementSuccessCharacter() {
    Image(
        painter = painterResource(R.drawable.party_settlement_success_icon),
        contentDescription = "전원 성공",
        modifier = Modifier.size(width = 73.dp, height = 104.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun SettlementSummaryCard(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().height(76.dp).background(Persimmon10, RoundedCornerShape(14.dp)).border(BorderStroke(1.dp, Persimmon.copy(alpha = 0.2f)), RoundedCornerShape(14.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettlementAmount("도전금 환급", "30,000P", DarkBrown50, BlackBrown, Modifier.weight(1f))
        SettlementAmount("성과 보너스", "+5,000P", Green, Green, Modifier.weight(1f))
    }
}

@Composable
private fun SettlementAmount(label: String, amount: String, labelColor: androidx.compose.ui.graphics.Color, amountColor: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = labelColor, fontFamily = Pretendard, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(amount, modifier = Modifier.padding(top = 3.dp), color = amountColor, fontFamily = Pretendard, fontSize = 17.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PartySettlementMemberCard(
    member: PartySettlementMemberUi,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth().height(56.dp).background(White, RoundedCornerShape(12.dp)).border(BorderStroke(1.dp, DarkBrown40), RoundedCornerShape(12.dp)).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).background(Persimmon10, CircleShape).border(1.dp, Persimmon, CircleShape), contentAlignment = Alignment.Center) {
            Image(painterResource(R.drawable.party_member_avatar), null, Modifier.size(38.dp), contentScale = ContentScale.Fit)
        }
        Text(member.name, modifier = Modifier.padding(start = 12.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text(member.bonus, color = Green, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(name = "파티 정산 전원 성공", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartySettlementScreenPreview() {
    OnulDo_FETheme { PartySettlementScreen(onBack = {}) }
}

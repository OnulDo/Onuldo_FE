package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.theme.*

@Composable
fun PartySettlementScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(SourCream).systemBarsPadding()) {
        Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
            OnulDoBackButton(Modifier.align(Alignment.CenterStart).padding(start = 20.dp), onClick = onBack)
            Text("파티 결과", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(R.drawable.ic_challenge_success_icon), null, Modifier.size(128.dp))
            Text("모두 함께 완주했어요!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = BlackBrown)
            Text("14일 동안 서로 응원하며 챌린지를 완료했어요", color = DarkBrown50, modifier = Modifier.padding(top = 8.dp))
            Row(Modifier.fillMaxWidth().padding(top = 24.dp).background(Persimmon10, RoundedCornerShape(16.dp)).padding(18.dp), horizontalArrangement = Arrangement.SpaceAround) {
                SettlementInfo("성공 인원", "5명")
                SettlementInfo("총 인증", "70회")
                SettlementInfo("내 환급", "12,000P")
            }
            Text("파티원 결과", Modifier.fillMaxWidth().padding(top = 26.dp, bottom = 12.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            listOf("민지" to "+2,000P", "서연" to "+2,000P", "지호" to "+2,000P", "수아" to "+2,000P", "도윤" to "+2,000P").forEach { (name, point) ->
                Row(Modifier.fillMaxWidth().padding(bottom = 10.dp).height(58.dp).background(White, RoundedCornerShape(14.dp)).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.party_member_avatar), null, Modifier.size(36.dp).clip(CircleShape))
                    Text(name, Modifier.weight(1f).padding(start = 10.dp), fontWeight = FontWeight.Bold)
                    Text(point, color = Persimmon, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable private fun SettlementInfo(label: String, value: String) = Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(label, color = DarkBrown50, fontSize = 12.sp); Text(value, color = Persimmon, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, modifier = Modifier.padding(top = 5.dp)) }

@Preview(name = "파티 정산 결과", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartySettlementScreenPreview() {
    OnulDo_FETheme { PartySettlementScreen {} }
}

package com.example.onuldo_fe.ui.screen.party.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.party.*
import com.example.onuldo_fe.ui.theme.*

@Composable
fun PartyWaitingMemberCard(member: PartyMemberUi, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(White, RoundedCornerShape(12.dp))
            .border(1.dp, DarkBrown40, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).background(Persimmon10, CircleShape).border(1.dp, Persimmon, CircleShape), contentAlignment = Alignment.Center) {
            Image(painterResource(R.drawable.party_member_avatar), null, Modifier.width(38.dp).height(37.dp).clip(CircleShape))
        }
        Text(member.name, Modifier.weight(1f).padding(start = 12.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        when {
            member.role == PartyMemberRole.Leader -> PartyWaitingStatusChip("파티장", Persimmon10, Persimmon)
            member.readyStatus == PartyReadyStatus.Ready -> PartyWaitingStatusChip("준비완료", Persimmon, SourCream)
            else -> PartyWaitingStatusChip("대기", DarkBrown10, DarkBrown50)
        }
    }
}

@Composable
private fun PartyWaitingStatusChip(text: String, background: Color, foreground: Color) {
    Box(Modifier.width(56.dp).height(22.dp).background(background, RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
        Text(text, color = foreground, fontFamily = Pretendard, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(name = "대기방 파티원", showBackground = true, widthDp = 390)
@Composable
private fun PartyWaitingMemberCardPreview() {
    OnulDo_FETheme {
        Box(Modifier.background(SourCream).padding(20.dp)) {
            PartyWaitingMemberCard(
                member = PartyMemberUi(
                    name = "서연",
                    role = PartyMemberRole.Member,
                    readyStatus = PartyReadyStatus.Ready
                )
            )
        }
    }
}

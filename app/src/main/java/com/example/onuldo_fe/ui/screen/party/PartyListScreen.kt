package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

@Composable
fun PartyListScreen(
    parties: List<PartyCardUi>,
    onCreateClick: () -> Unit,
    onInviteCodeClick: () -> Unit,
    onPartyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().background(SourCream).statusBarsPadding()) {
        Text("파티", modifier = Modifier.padding(start = 24.dp, top = 16.dp), color = BlackBrown, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onCreateClick,
                modifier = Modifier.weight(10f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Persimmon)
            ) { Text("＋  파티 만들기", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
            OutlinedButton(
                onClick = onInviteCodeClick,
                modifier = Modifier.weight(7f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, Persimmon)
            ) { Text("초대코드 입력", color = Persimmon, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(20.dp))
        Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(width = 4.dp, height = 19.dp).background(Persimmon, RoundedCornerShape(4.dp)))
            Spacer(Modifier.width(5.dp))
            Text("나의 파티", color = BlackBrown, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(parties, key = { it.id }) { party -> PartyListCard(party, onClick = { onPartyClick(party.id) }) }
        }
    }
}

@Composable
private fun PartyListCard(party: PartyCardUi, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(party.partyName, color = BlackBrown, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(7.dp))
            Text(party.challengeName, color = DarkBrown50, fontSize = 13.sp)
            Spacer(Modifier.weight(1f))
            Text(party.dDay, color = DarkBrown50, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(party.deadline, color = Persimmon.copy(alpha = .8f), fontSize = 13.sp)
            party.remainingText?.let {
                Spacer(Modifier.width(10.dp))
                Text(it, modifier = Modifier.background(Persimmon10, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 2.dp), color = Persimmon, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(party.totalMemberCount) { index ->
                val completed = index < party.completedMemberCount
                Box(
                    Modifier
                        .padding(end = 5.dp)
                        .size(33.dp)
                        .alpha(if (completed) 1f else .5f)
                        .background(Persimmon10, CircleShape)
                        .then(if (completed) Modifier.border(1.dp, Persimmon, CircleShape) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(R.drawable.party_member_avatar), null, Modifier.size(31.dp), contentScale = ContentScale.Fit)
                }
            }
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = {}, modifier = Modifier.size(width = 96.dp, height = 32.dp), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Persimmon)) {
                Text("인증하기", color = Persimmon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyListScreenPreview() {
    OnulDo_FETheme { PartyListScreen(samplePartyCards, {}, {}, {}) }
}

package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.theme.*

@Composable
fun PartyWaitingRoomScreen(
    ui: PartyWaitingRoomUi,
    isLeader: Boolean,
    onBack: () -> Unit,
    onPrimaryClick: () -> Unit
) {
    val clipboard = LocalClipboardManager.current

    Column(Modifier.fillMaxSize().background(SourCream).systemBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
            OnulDoBackButton(Modifier.padding(start = 20.dp), onClick = onBack)
            Text("파티 대기방", Modifier.padding(start = 14.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Column(Modifier.weight(1f).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(40.dp))
            Column(
                Modifier.fillMaxWidth().height(76.dp).background(Persimmon10, RoundedCornerShape(14.dp)).border(1.dp, Persimmon20, RoundedCornerShape(14.dp)).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(ui.partyName, color = BlackBrown, fontFamily = Pretendard, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    "모집중 · ${ui.members.size}/${ui.capacity}명 · ${ui.period} · 1인 ${"%,d".format(ui.deposit)}P",
                    Modifier.padding(top = 8.dp), color = DarkBrown, fontFamily = Pretendard, fontSize = 11.sp, fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth().height(88.dp).background(White, RoundedCornerShape(16.dp)).border(1.dp, DarkBrown40, RoundedCornerShape(16.dp)).padding(horizontal = 19.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("초대코드", color = DarkBrown50, fontFamily = Pretendard, fontSize = 12.sp)
                    Text(ui.inviteCode, Modifier.padding(top = 3.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Box(
                    Modifier.width(64.dp).height(36.dp).background(DarkBrown10, RoundedCornerShape(18.dp)).clickable { clipboard.setText(AnnotatedString(ui.inviteCode)) },
                    contentAlignment = Alignment.Center
                ) { Text("복사", color = DarkBrown, fontFamily = Pretendard, fontSize = 12.sp) }
            }

            Spacer(Modifier.height(21.dp))
            Text("파티원", Modifier.padding(start = 4.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            ui.members.forEachIndexed { index, member ->
                WaitingMemberCard(member = member, showReadyState = !isLeader)
                if (index != ui.members.lastIndex) Spacer(Modifier.height(8.dp))
            }
            Text(
                "전원이 모이면 파티장이 시작할 수 있어요",
                Modifier.fillMaxWidth().padding(top = 8.dp), color = DarkBrown50, fontFamily = Pretendard, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Box(Modifier.fillMaxWidth().height(138.dp).background(SourCream), contentAlignment = Alignment.TopCenter) {
            Button(
                onClick = onPrimaryClick,
                enabled = if (isLeader) ui.canStart else true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 40.dp).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Persimmon, contentColor = SourCream, disabledContainerColor = DarkBrown10, disabledContentColor = DarkBrown40)
            ) { Text(if (isLeader) "시작하기" else "준비완료", fontFamily = Pretendard, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun WaitingMemberCard(member: PartyMemberUi, showReadyState: Boolean) {
    Row(
        Modifier.fillMaxWidth().height(56.dp).background(White, RoundedCornerShape(12.dp)).border(1.dp, DarkBrown40, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).background(Persimmon10, CircleShape).border(1.dp, Persimmon, CircleShape), contentAlignment = Alignment.Center) {
            Image(painterResource(R.drawable.party_member_avatar), null, Modifier.width(38.dp).height(37.dp).clip(CircleShape))
        }
        Text(member.name, Modifier.weight(1f).padding(start = 12.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.13).sp)
        when {
            member.role == PartyMemberRole.Leader -> WaitingChip("파티장", Persimmon10, Persimmon)
            showReadyState && member.readyStatus == PartyReadyStatus.Ready -> WaitingChip("준비완료", Persimmon, SourCream)
        }
    }
}

@Composable
private fun WaitingChip(text: String, background: androidx.compose.ui.graphics.Color, foreground: androidx.compose.ui.graphics.Color) {
    Box(Modifier.width(56.dp).height(22.dp).background(background, RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
        Text(text, color = foreground, fontFamily = Pretendard, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(name = "대기방 - 파티장", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyWaitingRoomLeaderPreview() { OnulDo_FETheme { PartyWaitingRoomScreen(PartyWaitingRoomUi(), true, {}, {}) } }

@Preview(name = "대기방 - 파티원", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyWaitingRoomMemberPreview() { OnulDo_FETheme { PartyWaitingRoomScreen(PartyWaitingRoomUi(), false, {}, {}) } }

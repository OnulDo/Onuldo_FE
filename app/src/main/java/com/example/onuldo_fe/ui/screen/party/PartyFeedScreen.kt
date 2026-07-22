package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.theme.*

private data class PartyFeedItem(val name: String, val time: String, val verified: Boolean)
private val partyFeedItems = listOf(
    PartyFeedItem("민지", "오늘 오전 6:42", true), PartyFeedItem("서연", "오늘 오전 6:51", true),
    PartyFeedItem("지호", "오늘 오전 6:58", true), PartyFeedItem("수아", "오늘 오전 7:02", true),
    PartyFeedItem("도윤", "아직 인증 전", false)
)

@Composable
fun PartyFeedScreen(onBack: () -> Unit, onSettlementClick: () -> Unit) {
    Column(Modifier.fillMaxSize().background(SourCream).systemBarsPadding()) {
        Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
            OnulDoBackButton(Modifier.align(Alignment.CenterStart).padding(start = 20.dp), onClick = onBack)
            Text("안녕 러너 파티", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onSettlementClick, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)) { Text("정산", color = Persimmon) }
        }
        Text("30분 러닝", Modifier.padding(horizontal = 20.dp), color = DarkBrown50)
        TeamProgressCard(Modifier.padding(horizontal = 20.dp, vertical = 17.dp))
        Text("파티 인증 피드", Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(partyFeedItems) { item ->
                Column(Modifier.background(White, RoundedCornerShape(14.dp)).clip(RoundedCornerShape(14.dp))) {
                    Box(Modifier.fillMaxWidth().aspectRatio(1f).background(DarkBrown10), contentAlignment = Alignment.Center) {
                        Text(if (item.verified) "인증 이미지" else "아직 인증 전", color = DarkBrown40, fontSize = 12.sp)
                    }
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.party_member_avatar), null, Modifier.size(28.dp).clip(CircleShape))
                        Column(Modifier.padding(start = 8.dp)) { Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp); Text(item.time, color = if (item.verified) DarkBrown40 else Persimmon, fontSize = 10.sp) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamProgressCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(Persimmon10, RoundedCornerShape(14.dp))
            .border(1.dp, Persimmon.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "팀 진행률",
                color = Persimmon,
                fontFamily = Pretendard,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.44.sp
            )
            Text(
                "5명 중 4명 오늘 인증 완료",
                modifier = Modifier.padding(start = 8.dp),
                color = DarkBrown50,
                fontFamily = Pretendard,
                fontSize = 8.sp
            )
        }
        Text(
            "72%",
            color = BlackBrown,
            fontFamily = Pretendard,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.48).sp
        )
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(8.dp).background(White, CircleShape)) {
            Box(Modifier.fillMaxWidth(0.64f).height(8.dp).background(Persimmon, CircleShape))
        }
    }
}

@Preview(name = "파티 팀 진행률", showBackground = true, widthDp = 390)
@Composable
private fun TeamProgressCardPreview() {
    OnulDo_FETheme {
        Box(Modifier.background(SourCream).padding(20.dp)) {
            TeamProgressCard()
        }
    }
}

@Preview(name = "파티 인증 피드", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyFeedScreenPreview() {
    OnulDo_FETheme { PartyFeedScreen({}, {}) }
}

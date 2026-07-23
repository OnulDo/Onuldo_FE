package com.example.onuldo_fe.ui.screen.party

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.screen.party.component.PartyVerificationNoticeBottomSheet
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

private data class PartyChallengeBenefit(val title: String, val description: String)

private val partyChallengeBenefits = listOf(
    PartyChallengeBenefit("아침에 나만의 30분이 생겨요", "세상이 조용한 시간, 방해 없이 나에게 집중할 수 있어요"),
    PartyChallengeBenefit("마음의 여유가 생겨요", "허둥지둥 뛰는 아침 대신, 커피 한 잔의 여유를 챙겨요"),
    PartyChallengeBenefit("생체 리듬이 정렬돼요", "일찍 일어나면 밤에 잠도 잘 오는 선순환이 만들어져요"),
    PartyChallengeBenefit("하루의 주도권을 되찾아요", "‘시작 당한’ 게 아니라 ‘시작한’ 감각으로 살게 돼요")
)

private val partyChallengeRecommendations = listOf(
    "미라클 모닝을 여러 번 시도했지만 매번 3일을 못 넘긴 분",
    "출근·등교 전 늘 시간에 쫓기는 게 지친 분",
    "아침형 인간이 되고 싶어 자기 계발을 시작하려는 분",
    "혼자서는 자꾸 무너져서 함께할 동료가 필요한 분"
)

@Composable
fun PartyChallengeDetailScreen(
    challenge: PartyChallengeUi,
    onBack: () -> Unit,
    onParticipate: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    var showVerificationNotice by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(SourCream).systemBarsPadding()
    ) {
        item {
            Box(Modifier.fillMaxWidth().height(56.dp)) {
                OnulDoBackButton(
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp),
                    onClick = onBack
                )
            }
        }
        item {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 23.dp, bottom = 22.dp)) {
                Text(challenge.title, color = BlackBrown, fontFamily = Pretendard, fontSize = 26.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold)
                Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("생활루틴 챌린지", color = DarkBrown, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
                    Text("참여자 1,234명", modifier = Modifier.padding(start = 8.dp), color = DarkBrown, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 20.sp)
                }
            }
        }
        item { Box(Modifier.fillMaxWidth().height(1.dp).background(Persimmon.copy(alpha = 0.2f))) }
        item {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
                PartyDetailSectionTitle("이 챌린지는?")
                Row(Modifier.padding(top = 5.dp, start = 0.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(width = 4.dp, height = 19.dp).background(Persimmon, RoundedCornerShape(4.dp)))
                    Text(
                        "세상보다 먼저 눈뜨는 21일, 나만의 새벽 30분",
                        modifier = Modifier.padding(start = 5.dp),
                        color = BlackBrown,
                        fontFamily = Pretendard,
                        fontSize = 13.sp,
                        lineHeight = 24.sp
                    )
                }

                Spacer(Modifier.height(33.dp))
                PartyDetailSectionTitle("하면 좋은 점")
                Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    partyChallengeBenefits.forEach { benefit ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(benefit.title, color = DarkBrown, fontFamily = Pretendard, fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
                            Text(benefit.description, color = BlackBrown, fontFamily = Pretendard, fontSize = 13.sp, lineHeight = 19.sp)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
                PartyDetailSectionTitle("이런 분께 추천해요")
                Column(Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    partyChallengeRecommendations.forEach { recommendation ->
                        Text(recommendation, color = DarkBrown, fontFamily = Pretendard, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }

                Spacer(Modifier.height(30.dp))
                PartyVerificationMethodCard { showVerificationNotice = true }
            }
        }
        item {
            Button(
                onClick = onParticipate,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 10.dp, bottom = 48.dp).height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Persimmon, contentColor = SourCream)
            ) {
                Text("참여하기", fontFamily = Pretendard, fontSize = 17.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showVerificationNotice) {
        PartyVerificationNoticeBottomSheet(
            challengeTitle = challenge.title,
            onDismiss = { showVerificationNotice = false }
        )
    }
}

@Composable
private fun PartyDetailSectionTitle(text: String) {
    Text(text, color = BlackBrown, fontFamily = Pretendard, fontSize = 22.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun PartyVerificationMethodCard(onNoticeClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Persimmon10, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, Persimmon.copy(alpha = 0.2f)), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text("인증 방법", color = BlackBrown, fontFamily = Pretendard, fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
        Text("이렇게 찍어주세요", modifier = Modifier.padding(top = 8.dp), color = Persimmon, fontFamily = Pretendard, fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
        Text("침대와 개어진 이불 사진이 나오게 촬영하기", modifier = Modifier.padding(top = 2.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 13.sp)
        Image(
            painter = painterResource(R.drawable.party_challenge_detail_verification),
            contentDescription = "침대와 개어진 이불 인증 예시",
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp).size(width = 195.dp, height = 315.dp).clip(RoundedCornerShape(12.dp)).border(1.5.dp, Persimmon.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .height(53.dp)
                .background(White, RoundedCornerShape(14.dp))
                .border(1.5.dp, Persimmon.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                .clickable(onClick = onNoticeClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.party_challenge_detail_info), null, tint = Persimmon, modifier = Modifier.size(17.dp))
            Text("인증 유의사항 보기", modifier = Modifier.weight(1f), color = BlackBrown, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Icon(painterResource(R.drawable.back_btn), null, tint = Persimmon.copy(alpha = 0.2f), modifier = Modifier.size(width = 5.dp, height = 11.dp).rotate(180f))
        }
    }
}

@Preview(name = "파티 챌린지 상세", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyChallengeDetailScreenPreview() {
    OnulDo_FETheme {
        PartyChallengeDetailScreen(
            challenge = PartyChallengeUi("challenge-1", "새벽 6시 기상", "4주", 10_000),
            onBack = {},
            onParticipate = {}
        )
    }
}

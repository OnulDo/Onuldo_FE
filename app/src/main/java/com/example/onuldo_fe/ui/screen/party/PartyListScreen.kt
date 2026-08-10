package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import com.example.onuldo_fe.model.home.ChallengeStatus
import com.example.onuldo_fe.model.home.HomePartyChallenge
import com.example.onuldo_fe.model.home.HomePartyMember
import com.example.onuldo_fe.ui.screen.home.components.HomePartyCard
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.party.PartyCardUi
import com.example.onuldo_fe.viewmodel.party.samplePartyCards
import java.time.LocalTime

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PartyListScreen(
    parties: List<PartyCardUi>,
    onVerifyClick: (PartyCardUi) -> Unit,
    onCreateClick: () -> Unit,
    onInviteCodeClick: () -> Unit,
    onPartyClick: (String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier,
    partyCardContent: @Composable (PartyCardUi, () -> Unit) -> Unit = { party, onClick ->
        HomePartyCard(
            partyChallenge = party.toHomePartyChallenge(),
            onVerifyClick = { onVerifyClick(party) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        )
    }
) {
    val spacing = LocalSpacing.current
    val pullToRefreshState = rememberPullToRefreshState()
    // 챌린지 목록과 동일하게 당길 때 상단에 실제 인디케이터를 보여준다.
    // 탭 재진입 시의 조용한 재조회(SILENT)는 isRefreshing을 건드리지 않으므로 노출되지 않는다.
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = modifier.fillMaxSize(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                color = Persimmon
            )
        }
    ) {
    Column(Modifier.fillMaxSize().background(SourCream)) {
        Text("파티", modifier = Modifier.padding(start = spacing.spacing24, top = spacing.spacing16), color = BlackBrown, fontFamily = Pretendard, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        // TODO 디자인 시스템에 21dp 토큰이 추가되면 LocalSpacing으로 교체
        Spacer(Modifier.height(21.dp))
        Row(Modifier.padding(horizontal = spacing.spacing20), horizontalArrangement = Arrangement.spacedBy(spacing.spacing10)) {
            Button(
                onClick = onCreateClick,
                modifier = Modifier.weight(10f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Persimmon,
                    contentColor = SourCream
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.party_create_plus_icon),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                // TODO 디자인 시스템에 3dp 토큰이 추가되면 LocalSpacing으로 교체
                Spacer(Modifier.width(3.dp))
                Text("파티 만들기", fontFamily = Pretendard, fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onInviteCodeClick,
                modifier = Modifier.weight(7f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, Persimmon),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = White),
                contentPadding = PaddingValues(0.dp)
            ) { Text("초대코드 입력", color = Persimmon, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(spacing.spacing24))
        Row(Modifier.padding(horizontal = spacing.spacing20), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(width = 4.dp, height = 19.dp).background(Persimmon, RoundedCornerShape(4.dp)))
            Spacer(Modifier.width(5.dp))
            Text("나의 파티", color = BlackBrown, fontFamily = Pretendard, fontSize = 17.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
        }
        // TODO 디자인 시스템에 14dp 토큰이 추가되면 LocalSpacing으로 교체
        Spacer(Modifier.height(14.dp))
        if (isLoading) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Persimmon)
            }
        } else if (errorMessage != null) {
            Column(
                Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(errorMessage, color = DarkBrown50, fontFamily = Pretendard, fontSize = 13.sp)
                TextButton(onClick = onRetry) { Text("다시 시도", color = Persimmon) }
            }
        } else if (parties.isEmpty()) {
            PartyListEmptyContent(Modifier.fillMaxWidth().weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(parties, key = { it.id }) { party ->
                    partyCardContent(party) { onPartyClick(party.id) }
                }
            }
        }
    }
    }
}

@Composable
private fun PartyListEmptyContent(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    Column(
        // TODO 디자인 시스템에 96dp 토큰이 추가되면 LocalSpacing으로 교체
        modifier = modifier.padding(top = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Persimmon.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.party_empty_character),
                contentDescription = null,
                modifier = Modifier.size(width = 82.dp, height = 95.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(spacing.spacing8))
        Text(
            text = "아직 시작한 파티가 없어요",
            color = BlackBrown,
            fontFamily = Pretendard,
            fontSize = 22.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "친구들과 함께 도전하여 더욱 즐겁게\n인증하세요!",
            color = com.example.onuldo_fe.ui.theme.DarkBrown,
            fontFamily = Pretendard,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Normal,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// API에서 남은 일수와 시간을 숫자 타입으로 제공하면 문자열 파싱 대신 응답 값을 직접 전달
private fun PartyCardUi.toHomePartyChallenge() = HomePartyChallenge(
    title = partyName,
    subtitle = challengeName,
    remainingDays = dDay.filter(Char::isDigit).toIntOrNull() ?: 0,
    deadlineAt = deadline.toLocalTimeOrNull(),
    completedMemberCount = completedMemberCount,
    totalMemberCount = totalMemberCount,
    status = verificationStatus,
    remainingMinutes = remainingText.toRemainingMinutes(),
    canVerify = verificationStatus == ChallengeStatus.NeedCertification &&
        myDailyStatus.equals("WAITING", ignoreCase = true) &&
        challengeId > 0L,
    members = members.map { member ->
        HomePartyMember(
            memberId = member.userId.toString(),
            profileImageUrl = member.profileImageUrl,
            defaultCharacterId = null,
            isVerifiedToday = member.isVerifiedToday
        )
    },
    challengeId = challengeId
)

private fun String.toLocalTimeOrNull(): LocalTime? {
    val match = Regex("""(\d{1,2}):(\d{2})""").find(this) ?: return null
    return runCatching {
        LocalTime.of(match.groupValues[1].toInt(), match.groupValues[2].toInt())
    }.getOrNull()
}

private fun String?.toRemainingMinutes(): Int? {
    if (this == null) return null
    val hours = Regex("""(\d+)\s*시간""").find(this)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    val minutes = Regex("""(\d+)\s*분""").find(this)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    return (hours * 60 + minutes).takeIf { it > 0 }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyListScreenPreview() {
    OnulDo_FETheme { PartyListScreen(samplePartyCards, {}, {}, {}, {}) }
}

@Preview(name = "파티 목록 - 빈 상태", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyListEmptyScreenPreview() {
    OnulDo_FETheme { PartyListScreen(emptyList(), {}, {}, {}, {}) }
}

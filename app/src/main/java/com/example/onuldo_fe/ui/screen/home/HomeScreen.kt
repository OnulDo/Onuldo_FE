package com.example.onuldo_fe.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.data.home.api.FakeHomeApi
import com.example.onuldo_fe.data.home.dummy.FakeHomeScenario
import com.example.onuldo_fe.repository.home.HomeRepositoryImpl
import com.example.onuldo_fe.ui.component.home.EmptyChallengeContent
import com.example.onuldo_fe.ui.component.home.HomeChallengeCard
import com.example.onuldo_fe.ui.component.home.HomeCompletedChallengeCard
import com.example.onuldo_fe.ui.component.home.HomeHeader
import com.example.onuldo_fe.ui.component.home.HomePartyCard
import com.example.onuldo_fe.ui.component.home.SettlementCompleteCard
import com.example.onuldo_fe.ui.component.home.TodayChallengeCard
import com.example.onuldo_fe.viewmodel.home.HomeUiState
import com.example.onuldo_fe.viewmodel.home.toUiState
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNotificationClick: () -> Unit = {},
    onBrowseChallengesClick: () -> Unit = {},
    onSettlementResultClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
        // .navigationBarsPadding()    ← 제거 (Scaffold가 이미 처리)
    ) {
        // 홈 API 상태에 따라 기본 홈과 빈 홈 분기
        if (uiState.hasHomeContent) {
            HomeContent(uiState, onNotificationClick, onSettlementResultClick)
        } else {
            EmptyHomeContent(
                userName = uiState.userName,
                onNotificationClick = onNotificationClick,
                onBrowseChallengesClick = onBrowseChallengesClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun EmptyHomeContent(
    userName: String,
    onNotificationClick: () -> Unit,
    onBrowseChallengesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        HomeHeader(
            userName = userName,
            onNotificationClick = onNotificationClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 34.dp)
        )

        EmptyChallengeContent(
            onBrowseChallengesClick = onBrowseChallengesClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = 56.dp, end = 56.dp, top = 216.dp)
        )
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onNotificationClick: () -> Unit,
    onSettlementResultClick: (String) -> Unit
) {
    val isAllCompleted = uiState.isAllCompleted

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // 모든 홈 상태에서 공통 헤더 유지
        HomeHeader(
            userName = uiState.userName,
            onNotificationClick = onNotificationClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 34.dp, bottom = 17.dp)
        )

        // 오늘 집계 데이터가 있을 때 오늘의 챌린지 카드 노출
        uiState.todayChallenge?.let { todayChallenge ->
            TodayChallengeCard(
                todayChallenge = todayChallenge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }

        // 확인하지 않은 파티 정산 결과가 있을 때 배너 노출
        uiState.settlementBanner?.let { banner ->
            Spacer(modifier = Modifier.height(18.dp))
            SettlementCompleteCard(
                partyName = banner.partyName,
                onClick = { onSettlementResultClick(banner.resultId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }

        // 진행 중인 파티 챌린지가 있을 때 파티 목록 노출
        ChallengeSection(
            title = "함께하는 파티",
            visible = !isAllCompleted && uiState.partyChallenges.isNotEmpty(),
            topSpacing = if (uiState.settlementBanner != null) 18.dp else 28.dp
        ) {
            uiState.partyChallenges.forEach { partyChallenge ->
                HomePartyCard(partyChallenge, Modifier.fillMaxWidth())
            }
        }

        // 진행 중인 개인 챌린지가 있을 때 개인 목록 노출
        ChallengeSection(
            title = "나의 챌린지",
            visible = !isAllCompleted && uiState.challenges.isNotEmpty(),
            topSpacing = if (uiState.partyChallenges.isNotEmpty()) 18.dp else 28.dp
        ) {
            uiState.challenges.forEach { challenge ->
                HomeChallengeCard(challenge, Modifier.fillMaxWidth())
            }
        }

        // 완료 상태일 때 완료 챌린지 목록 노출
        ChallengeSection(
            title = stringResource(R.string.home_completed_challenge_title),
            visible = isAllCompleted,
            topSpacing = 26.dp,
            itemSpacing = 8.dp
        ) {
            uiState.completedChallenges.forEach { completedChallenge ->
                HomeCompletedChallengeCard(completedChallenge, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ChallengeSection(
    title: String,
    visible: Boolean,
    topSpacing: androidx.compose.ui.unit.Dp = 28.dp,
    itemSpacing: androidx.compose.ui.unit.Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!visible) return

    Spacer(modifier = Modifier.height(topSpacing))
    SectionTitle(text = title, modifier = Modifier.padding(horizontal = 20.dp))
    Spacer(modifier = Modifier.height(14.dp))
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        content = content
    )
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(19.dp)
                .background(Persimmon, RoundedCornerShape(4.5.dp))
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            color = BlackBrown,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 17.sp,
            lineHeight = 20.sp
        )
    }
}

@Preview(name = "Home With Content", showBackground = true, showSystemUi = true, widthDp = 390, heightDp = 938)
@Composable
private fun HomeScreenPreview() {
    HomeScenarioPreview(FakeHomeScenario.Default)
}

@Preview(
    name = "Home Party Integrated",
    showBackground = true,
    showSystemUi = true,
    widthDp = 390,
    heightDp = 1338
)
@Composable
private fun HomeScreenPartyPreview() {
    // Default 더미 데이터에 파티 챌린지와 정산 완료 배너가 포함되어 파티 통합 상태 확인
    HomeScenarioPreview(FakeHomeScenario.Default)
}

@Preview(name = "Home Empty", showBackground = true, showSystemUi = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenEmptyPreview() {
    HomeScenarioPreview(FakeHomeScenario.Empty)
}

@Preview(name = "Home All Completed", showBackground = true, showSystemUi = true, widthDp = 390, heightDp = 938)
@Composable
private fun HomeScreenAllCompletedPreview() {
    HomeScenarioPreview(FakeHomeScenario.AllCompleted)
}

@Composable
private fun HomeScenarioPreview(scenario: FakeHomeScenario) {
    val repository = HomeRepositoryImpl(FakeHomeApi(scenario))
    OnulDo_FETheme { HomeScreen(uiState = repository.getHome().toUiState()) }
}

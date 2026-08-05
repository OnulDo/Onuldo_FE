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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.runBlocking
import com.example.onuldo_fe.R
import com.example.onuldo_fe.data.home.api.FakeHomeApi
import com.example.onuldo_fe.data.home.dummy.FakeHomeScenario
import com.example.onuldo_fe.repository.home.HomeRepositoryImpl
import com.example.onuldo_fe.ui.screen.home.components.EmptyChallengeContent
import com.example.onuldo_fe.ui.screen.home.components.HomeChallengeCard
import com.example.onuldo_fe.ui.screen.home.components.HomeCompletedChallengeCard
import com.example.onuldo_fe.ui.screen.home.components.HomeHeader
import com.example.onuldo_fe.ui.screen.home.components.HomePartyCard
import com.example.onuldo_fe.ui.screen.home.components.SettlementCompleteCard
import com.example.onuldo_fe.ui.screen.home.components.TodayChallengeCard
import com.example.onuldo_fe.viewmodel.home.HomeUiState
import com.example.onuldo_fe.viewmodel.home.toUiState
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(
    uiState: HomeUiState,
    onNotificationClick: () -> Unit = {},
    onBrowseChallengesClick: () -> Unit = {},
    onSettlementResultClick: (Long) -> Unit = {},
    onVerifyClick: () -> Unit = {},
    onRefresh: () -> Unit = {},
    scrollToTopKey: Int = 0,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
    ) {
        // 홈 API 상태에 따라 기본 홈과 빈 홈 분기
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Persimmon
            )
        } else if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                modifier = Modifier.align(Alignment.Center),
                color = BlackBrown
            )
        } else if (uiState.hasHomeContent) {
            HomeContent(
                uiState = uiState,
                onNotificationClick = onNotificationClick,
                onSettlementResultClick = onSettlementResultClick,
                onVerifyClick = onVerifyClick,
                scrollToTopKey = scrollToTopKey
            )
        } else {
            EmptyHomeContent(
                userName = uiState.userName,
                profileImageUrl = uiState.userProfileImageUrl,
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
    profileImageUrl: String?,
    onNotificationClick: () -> Unit,
    onBrowseChallengesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    // TODO: 디자인 시스템에 없는 34·56dp 여백 토큰 추가 후 교체

    Box(modifier = modifier) {
        HomeHeader(
            userName = userName,
            profileImageUrl = profileImageUrl,
            onNotificationClick = onNotificationClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = spacing.spacing20, end = spacing.spacing20, top = 34.dp)
        )

        EmptyChallengeContent(
            onBrowseChallengesClick = onBrowseChallengesClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = spacing.spacing20, end = spacing.spacing20, top = 216.dp)
        )
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onNotificationClick: () -> Unit,
    onSettlementResultClick: (Long) -> Unit,
    onVerifyClick: () -> Unit,
    scrollToTopKey: Int
) {
    val spacing = LocalSpacing.current
    val isAllCompleted = uiState.isAllCompleted
    val scrollState = rememberScrollState()
    // TODO: 디자인 시스템에 없는 17·34dp 여백 토큰 추가 후 교체

    LaunchedEffect(scrollToTopKey) {
        if (scrollToTopKey > 0) {
            // 파티 시작 후 홈으로 돌아오면 이전 스크롤 위치 대신 상단 표시
            scrollState.scrollTo(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = spacing.spacing24)
    ) {
        // 모든 홈 상태에서 공통 헤더 유지
        HomeHeader(
            userName = uiState.userName,
            profileImageUrl = uiState.userProfileImageUrl,
            onNotificationClick = onNotificationClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = spacing.spacing20, end = spacing.spacing20, top = 34.dp, bottom = 17.dp)
        )

        // 오늘 집계 데이터가 있을 때 오늘의 챌린지 카드 노출
        uiState.todayChallenge?.let { todayChallenge ->
            TodayChallengeCard(
                todayChallenge = todayChallenge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spacing20)
            )
        }

        // 확인하지 않은 파티 정산 결과가 있을 때 배너 노출
        uiState.settlementBanner?.let { banner ->
            Spacer(modifier = Modifier.height(spacing.spacing18))
            SettlementCompleteCard(
                partyName = banner.partyName,
                onClick = { onSettlementResultClick(banner.partyId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spacing20)
            )
        }

        // 진행 중인 파티 챌린지가 있을 때 파티 목록 노출
        ChallengeSection(
            title = "함께하는 파티",
            visible = !isAllCompleted && uiState.partyChallenges.isNotEmpty(),
            topSpacing = if (uiState.settlementBanner != null) spacing.spacing18 else spacing.spacing28,
            itemSpacing = spacing.spacing12
        ) {
            uiState.partyChallenges.forEach { partyChallenge ->
                HomePartyCard(
                    partyChallenge = partyChallenge,
                    modifier = Modifier.fillMaxWidth(),
                    onVerifyClick = onVerifyClick
                )
            }
        }

        // 진행 중인 개인 챌린지가 있을 때 개인 목록 노출
        ChallengeSection(
            title = "나의 챌린지",
            visible = !isAllCompleted && uiState.challenges.isNotEmpty(),
            topSpacing = if (uiState.partyChallenges.isNotEmpty()) spacing.spacing18 else spacing.spacing28,
            itemSpacing = spacing.spacing12
        ) {
            uiState.challenges.forEach { challenge ->
                HomeChallengeCard(
                    challenge = challenge,
                    modifier = Modifier.fillMaxWidth(),
                    onVerifyClick = onVerifyClick
                )
            }
        }

        // 완료 상태일 때 완료 챌린지 목록 노출
        ChallengeSection(
            title = stringResource(R.string.home_completed_challenge_title),
            visible = isAllCompleted,
            topSpacing = spacing.spacing26,
            itemSpacing = spacing.spacing8
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
    topSpacing: androidx.compose.ui.unit.Dp,
    itemSpacing: androidx.compose.ui.unit.Dp,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!visible) return
    val spacing = LocalSpacing.current
    // TODO: 디자인 시스템에 없는 5·14dp 여백 토큰 추가 후 교체

    Spacer(modifier = Modifier.height(topSpacing))
    SectionTitle(text = title, modifier = Modifier.padding(horizontal = spacing.spacing20))
    Spacer(modifier = Modifier.height(14.dp))
    Column(
        modifier = Modifier.padding(horizontal = spacing.spacing20),
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
    val uiState = runBlocking { repository.getHome().toUiState() }
    OnulDo_FETheme { HomeScreen(uiState = uiState) }
}

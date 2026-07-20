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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.screen.home.component.EmptyChallengeContent
import com.example.onuldo_fe.ui.screen.home.component.HomeChallengeCard
import com.example.onuldo_fe.ui.screen.home.component.HomeCompletedChallengeCard
import com.example.onuldo_fe.ui.screen.home.component.HomeHeader
import com.example.onuldo_fe.ui.screen.home.component.HomePartyCard
import com.example.onuldo_fe.ui.screen.home.component.SettlementCompleteCard
import com.example.onuldo_fe.ui.screen.home.component.TodayChallengeCard
import com.example.onuldo_fe.ui.screen.home.data.repository.FakeHomeRepository
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNotificationClick: () -> Unit = {},
    onBrowseChallengesClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (uiState.hasHomeContent) {
            HomeContent(uiState, onNotificationClick)
        } else {
            EmptyHomeContent(
                onNotificationClick = onNotificationClick,
                onBrowseChallengesClick = onBrowseChallengesClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun EmptyHomeContent(
    onNotificationClick: () -> Unit,
    onBrowseChallengesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        HomeHeader(
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
    onNotificationClick: () -> Unit
) {
    val isAllCompleted = uiState.isAllCompleted

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        if (isAllCompleted) {
            Spacer(modifier = Modifier.height(51.dp))
        } else {
            HomeHeader(
                onNotificationClick = onNotificationClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 34.dp, bottom = 17.dp)
            )
        }

        uiState.todayChallenge?.let { todayChallenge ->
            TodayChallengeCard(
                todayChallenge = todayChallenge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }

        if (!isAllCompleted && uiState.completedChallenges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            SettlementCompleteCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }

        ChallengeSection(
            title = "함께하는 파티",
            visible = !isAllCompleted && uiState.partyChallenges.isNotEmpty()
        ) {
            uiState.partyChallenges.forEach { partyChallenge ->
                HomePartyCard(partyChallenge, Modifier.fillMaxWidth())
            }
        }

        ChallengeSection(
            title = "나의 챌린지",
            visible = !isAllCompleted && uiState.challenges.isNotEmpty()
        ) {
            uiState.challenges.forEach { challenge ->
                HomeChallengeCard(challenge, Modifier.fillMaxWidth())
            }
        }

        ChallengeSection(
            title = "완료한 챌린지",
            visible = isAllCompleted
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
    content: @Composable ColumnScope.() -> Unit
) {
    if (!visible) return

    Spacer(modifier = Modifier.height(24.dp))
    SectionTitle(text = title, modifier = Modifier.padding(horizontal = 20.dp))
    Spacer(modifier = Modifier.height(14.dp))
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
    val repository = FakeHomeRepository()
    OnulDo_FETheme {
        HomeScreen(
            uiState = HomeUiState(
                todayChallenge = repository.getTodayChallenge(),
                partyChallenges = repository.getPartyChallenges(),
                challenges = repository.getChallenges(),
                completedChallenges = repository.getCompletedChallenges()
            )
        )
    }
}

@Preview(name = "Home Empty", showBackground = true, showSystemUi = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenEmptyPreview() {
    OnulDo_FETheme { HomeScreen(uiState = HomeUiState()) }
}

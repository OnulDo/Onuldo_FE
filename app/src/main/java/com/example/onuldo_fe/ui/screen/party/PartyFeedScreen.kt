package com.example.onuldo_fe.ui.screen.party

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.screen.party.components.PartyFeedCard
import com.example.onuldo_fe.viewmodel.party.PartyFeedItemUi
import com.example.onuldo_fe.ui.theme.*
import com.example.onuldo_fe.viewmodel.party.PartyProgressUiState

@Composable
fun PartyFeedScreen(
    partyName: String,
    challengeName: String,
    progress: PartyProgressUiState,
    feedItems: List<PartyFeedItemUi>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    onBack: () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(Modifier.fillMaxSize().background(SourCream)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
        ) {
            OnulDoBackButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = 13.dp),
                onClick = onBack
            )
            Text(
                text = partyName,
                modifier = Modifier.offset(x = spacing.spacing20, y = 47.dp),
                color = BlackBrown,
                style = OnulDoTypography.headline3Bold
            )
            Text(
                text = challengeName,
                modifier = Modifier.offset(x = spacing.spacing20, y = 79.dp),
                color = DarkBrown70,
                style = OnulDoTypography.caption4Medium
            )
        }
        TeamProgressCard(
            progressPercent = progress.progressPercent,
            completedMemberCount = progress.completedMemberCount,
            totalMemberCount = progress.totalMemberCount,
            modifier = Modifier.padding(
                start = spacing.spacing20,
                end = spacing.spacing20,
                bottom = spacing.spacing16
            )
        )
        when {
            isLoading -> Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Persimmon)
            }
            errorMessage != null -> Column(
                Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(errorMessage, color = DarkBrown, style = OnulDoTypography.caption1Regular)
                TextButton(onClick = onRetry) { Text("다시 시도", color = Persimmon) }
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = spacing.spacing20, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(spacing.spacing12),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(feedItems, key = { it.memberId }) { item ->
                    PartyFeedCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun TeamProgressCard(
    progressPercent: Int,
    completedMemberCount: Int,
    totalMemberCount: Int,
    modifier: Modifier = Modifier
) {
    val progress = (progressPercent / 100f).coerceIn(0f, 1f)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(Persimmon10, RoundedCornerShape(14.dp))
            .border(1.dp, Persimmon.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(horizontal = LocalSpacing.current.spacing20, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "팀 진행률",
                color = Persimmon,
                style = OnulDoTypography.caption3Bold,
                letterSpacing = 0.44.sp
            )
            // Caption4/Regular
            Text(
                "${totalMemberCount}명 중 ${completedMemberCount}명 오늘 인증 완료",
                modifier = Modifier.padding(start = 8.dp),
                color = DarkBrown50,
                style = OnulDoTypography.caption4Regular
            )
        }
        Text(
            "$progressPercent%",
            color = BlackBrown,
            style = OnulDoTypography.headline3Bold,
            letterSpacing = (-0.48).sp
        )
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(8.dp).background(White, CircleShape)) {
            Box(Modifier.fillMaxWidth(progress).height(8.dp).background(Persimmon, CircleShape))
        }
    }
}

@Preview(name = "파티 팀 진행률", showBackground = true, widthDp = 390)
@Composable
private fun TeamProgressCardPreview() {
    OnulDo_FETheme {
        Box(Modifier.background(SourCream).padding(20.dp)) {
            TeamProgressCard(progressPercent = 72, completedMemberCount = 4, totalMemberCount = 5)
        }
    }
}

@Preview(name = "파티 인증 피드", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyFeedScreenPreview() {
    OnulDo_FETheme {
        PartyFeedScreen(
            partyName = "갓생팟",
            challengeName = "새벽 6시 기상",
            progress = PartyProgressUiState(72, 4, 5),
            feedItems = listOf(
                PartyFeedItemUi("민지", "2시간 전", imageRes = R.drawable.party_feed_minji),
                PartyFeedItemUi("하늘", "미인증", memberId = "current-user")
            ),
            onBack = {}
        )
    }
}

package com.example.onuldo_fe.ui.screen.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.component.OnulDoMediumButton
import com.example.onuldo_fe.ui.screen.record.component.RecordTabRow
import com.example.onuldo_fe.ui.screen.record.data.CompleteRecord
import com.example.onuldo_fe.ui.screen.record.data.ProgressRecord
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun RecordScreen(
    progressList: List<ProgressRecord>,
    completeList: List<CompleteRecord>,
    totalCompletedCount: Int = 0,
    successRate: Int = 0,
    totalSavedAmount: Int = 0,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    onBrowseChallenges: () -> Unit = {},
    initialTab: RecordTab = RecordTab.PROGRESS
) {
    val spacing = LocalSpacing.current
    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }

    Column(Modifier.fillMaxSize().padding(top = 40.dp, start = 20.dp, end = 20.dp)) {
        Text("기록", color = BlackBrown,style = OnulDoTypography.headline3Bold,)
        Spacer(Modifier.height(6.dp))
        Text("내 챌린지 기록을 확인하세요", color = DarkBrown, style = OnulDoTypography.caption1Regular)
        Spacer(Modifier.height(spacing.spacing24))
        RecordTabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        Spacer(Modifier.height(spacing.spacing16))

        when {
            isLoading -> CenteredContent { CircularProgressIndicator() }
            errorMessage != null -> CenteredContent {
                Text(
                    text = errorMessage,
                    style = OnulDoTypography.title1Bold,
                    color = BlackBrown,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(spacing.spacing16))
                OnulDoMediumButton(text = "다시 시도", onClick = onRetry)
            }
            selectedTab == RecordTab.PROGRESS && progressList.isEmpty() -> EmptyRecordView(
                title = "진행 중인 챌린지가 없어요",
                description = "새로운 챌린지를 찾아 시작해보세요!",
                onBrowseChallenges = onBrowseChallenges
            )
            selectedTab == RecordTab.PROGRESS -> ProgressRecordScreen(progressList)
            completeList.isEmpty() -> EmptyRecordView(
                title = "완료한 챌린지가 없어요",
                description = "챌린지를 완료하면 기록을 확인할 수 있어요.",
                onBrowseChallenges = onBrowseChallenges
            )
            else -> CompleteRecordScreen(
                completeList = completeList,
                totalCompletedCount = totalCompletedCount,
                successRate = successRate,
                totalSavedAmount = totalSavedAmount
            )
        }
    }
}

@Composable
private fun CenteredContent(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun RecordScreenPreview() {
    OnulDo_FETheme {
        RecordScreen(progressList = emptyList(), completeList = emptyList())
    }
}
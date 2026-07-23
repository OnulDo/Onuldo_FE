package com.example.onuldo_fe.ui.screen.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.screen.record.component.CompleteFilter
import com.example.onuldo_fe.ui.screen.record.component.CompleteFilterRow
import com.example.onuldo_fe.ui.screen.record.component.RecordSummaryCard
import com.example.onuldo_fe.ui.screen.record.component.RecordTabRow
import com.example.onuldo_fe.ui.screen.record.data.CompleteRecord
import com.example.onuldo_fe.ui.screen.record.data.ProgressRecord
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import kotlin.collections.emptyList

@Composable
fun RecordScreen(
    progressList: List<ProgressRecord>,
    completeList: List<CompleteRecord>,
    initialTab: RecordTab = RecordTab.PROGRESS
) {
    val spacing = LocalSpacing.current

    var selectedTab by remember(initialTab) {
        mutableStateOf(initialTab)
    }

    Column(
        modifier = Modifier
            .padding(
                top = 40.dp,
                start = 20.dp,
                end = 20.dp
            )
    ) {
        Text(
            text = "기록",
            color = BlackBrown,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(spacing.spacing8))

        Text(
            text = "내 챌린지 기록을 확인하세요",
            color = DarkBrown,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(spacing.spacing24))

        RecordTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(spacing.spacing16))

        when (selectedTab) {
            RecordTab.PROGRESS -> {
                if (progressList.isEmpty()) {
                    EmptyRecordView(
                        title = "진행 중인 챌린지가 없어요",
                        description = "탐색 탭에서 새로운 챌린지를\n찾아 시작해보세요!"
                    )
                } else {
                    ProgressRecordScreen(
                        progressList = progressList
                    )
                }
            }

            RecordTab.COMPLETE -> {
                if (completeList.isEmpty()) {
                    EmptyRecordView(
                        title = "완료한 챌린지가 없어요",
                        description = "첫 챌린지를 완주하고\n성취 기록을 쌓아보세요!"
                    )
                } else {
                    CompleteRecordScreen(
                        completeList = completeList
                    )
                }
            }
        }
    }
}
@Preview(name = "진행중", showBackground = true, showSystemUi = true)
@Composable
private fun RecordProgressPreview() {
    OnulDo_FETheme {
        RecordScreen(
            progressList = listOf(
                ProgressRecord(
                    category = "생활루틴",
                    title = "새벽 6시 기상",
                    dDay = 15,
                    progress = 85,
                    point = 2000,
                    isTodayVerified = true
                )
            ),
            completeList = emptyList()
        )
    }
}

@Preview(name = "완료", showBackground = true, showSystemUi = true)
@Composable
private fun RecordCompletePreview() {
    OnulDo_FETheme {
        RecordScreen(
            progressList = emptyList(),
            completeList = listOf(
                CompleteRecord(
                    isSuccess = true,
                    title = "독서 30분",
                    progress = 100,
                    completeDate = "2026.07.22",
                    point = 2000
                )
            ),
            initialTab = RecordTab.COMPLETE
        )
    }
}
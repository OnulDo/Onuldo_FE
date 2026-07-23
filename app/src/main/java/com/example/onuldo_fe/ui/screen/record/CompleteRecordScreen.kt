package com.example.onuldo_fe.ui.screen.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.onuldo_fe.ui.screen.record.component.CompleteFilter
import com.example.onuldo_fe.ui.screen.record.component.CompleteFilterRow
import com.example.onuldo_fe.ui.screen.record.component.CompleteRecordCard
import com.example.onuldo_fe.ui.screen.record.component.RecordSummaryCard
import com.example.onuldo_fe.ui.screen.record.data.CompleteRecord
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import kotlin.collections.filter

@Composable
fun CompleteRecordScreen(
    completeList: List<CompleteRecord>
) {
    val spacing = LocalSpacing.current

    var selectedFilter by remember {
        mutableStateOf(CompleteFilter.ALL)
    }

    val filteredList = when (selectedFilter) {
        CompleteFilter.ALL -> completeList
        CompleteFilter.SUCCESS -> completeList.filter { it.isSuccess }
        CompleteFilter.FAIL -> completeList.filter { !it.isSuccess }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        RecordSummaryCard(
            completeCount = completeList.size,
            successRate = if (completeList.isEmpty()) {
                0
            } else {
                (completeList.count { it.isSuccess } * 100) / completeList.size
            },
            totalPoint = completeList.sumOf { it.point }
        )

        Spacer(modifier = Modifier.height(spacing.spacing24))

        CompleteFilterRow(
            selectedFilter = selectedFilter,
            allCount = completeList.size,
            successCount = completeList.count { it.isSuccess },
            failCount = completeList.count { !it.isSuccess },
            onFilterSelected = {
                selectedFilter = it
            }
        )

        Spacer(modifier = Modifier.height(spacing.spacing16))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.spacing12)
        ) {
            items(filteredList) { record ->
                CompleteRecordCard(
                    isSuccess = record.isSuccess,
                    title = record.title,
                    progress = record.progress,
                    completeDate = record.completeDate,
                    point = record.point
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompleteRecordScreenPreview() {
    OnulDo_FETheme {
        CompleteRecordScreen(
            completeList = listOf(
                CompleteRecord(
                    isSuccess = true,
                    title = "하루 물 2L 마시기",
                    progress = 100,
                    completeDate = "2026.07.22",
                    point = 5000
                ),
                CompleteRecord(
                    isSuccess = false,
                    title = "매일 만보 걷기",
                    progress = 70,
                    completeDate = "2026.07.18",
                    point = -1000
                )
            )
        )
    }
}
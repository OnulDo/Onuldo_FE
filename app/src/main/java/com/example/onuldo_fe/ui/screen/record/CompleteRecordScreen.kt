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

@Composable
fun CompleteRecordScreen(
    completeList: List<CompleteRecord>,
    totalCompletedCount: Int,
    successRate: Int,
    totalSavedAmount: Int
) {
    val spacing = LocalSpacing.current
    var selectedFilter by remember { mutableStateOf(CompleteFilter.ALL) }
    val successCount = completeList.count { it.isSuccess }
    val failCount = completeList.count { !it.isSuccess }
    val filteredList = when (selectedFilter) {
        CompleteFilter.ALL -> completeList
        CompleteFilter.SUCCESS -> completeList.filter { it.isSuccess }
        CompleteFilter.FAIL -> completeList.filter { !it.isSuccess }
    }

    Column(Modifier.fillMaxSize()) {
        RecordSummaryCard(
            completeCount = totalCompletedCount,
            successRate = successRate,
            totalPoint = totalSavedAmount
        )
        Spacer(Modifier.height(spacing.spacing24))
        CompleteFilterRow(
            selectedFilter = selectedFilter,
            allCount = completeList.size,
            successCount = successCount,
            failCount = failCount,
            onFilterSelected = { selectedFilter = it }
        )
        Spacer(Modifier.height(spacing.spacing16))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.spacing12)
        ) {
            items(filteredList, key = { it.participationId }) { record ->
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
                CompleteRecord(participationId = 1, challengeId = 12, isSuccess = true,
                    title = "매일 6시 기상", progress = 92,
                    completeDate = "2026-08-10", point = 30000)
            ),
            totalCompletedCount = 8,
            successRate = 75,
            totalSavedAmount = 210000
        )
    }
}
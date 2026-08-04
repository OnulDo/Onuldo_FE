package com.example.onuldo_fe.ui.screen.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.onuldo_fe.ui.screen.record.component.ProgressRecordCard
import com.example.onuldo_fe.ui.screen.record.data.ProgressRecord
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun ProgressRecordScreen(
    progressList: List<ProgressRecord>
) {
    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.spacing12)
    ) {
        items(progressList, key = { it.participationId }) { record ->
            ProgressRecordCard(
                category = record.category,
                title = record.title,
                dDay = record.dDay,
                progress = record.progress,
                depositAmount = record.depositAmount,
                isTodayVerified = record.isTodayVerified
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgressRecordScreenPreview() {
    OnulDo_FETheme {
        ProgressRecordScreen(
            progressList = listOf(
                ProgressRecord(
                    participationId = 1,
                    challengeId = 101,
                    category = "운동",
                    title = "매일 만보 걷기",
                    dDay = 7,
                    progress = 65,
                    depositAmount = 2000,
                    isTodayVerified = true
                ),
                ProgressRecord(
                    participationId = 2,
                    challengeId = 102,
                    category = "공부",
                    title = "영단어 외우기",
                    dDay = 3,
                    progress = 40,
                    depositAmount = 1000,
                    isTodayVerified = false
                )
            )
        )
    }
}
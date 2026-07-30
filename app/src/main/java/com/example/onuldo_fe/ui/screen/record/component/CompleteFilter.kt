package com.example.onuldo_fe.ui.screen.record.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.White

enum class CompleteFilter {
    ALL,
    SUCCESS,
    FAIL
}

@Composable
fun CompleteFilterRow(
    selectedFilter: CompleteFilter,
    allCount: Int,
    successCount: Int,
    failCount: Int,
    onFilterSelected: (CompleteFilter) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompleteFilterChip(
            text = "전체",
            count = allCount,
            selected = selectedFilter == CompleteFilter.ALL,
            onClick = {
                onFilterSelected(CompleteFilter.ALL)
            }
        )

        CompleteFilterChip(
            text = "성공",
            selected = selectedFilter == CompleteFilter.SUCCESS,
            count = successCount,
            onClick = {
                onFilterSelected(CompleteFilter.SUCCESS)
            }
        )

        CompleteFilterChip(
            text = "실패",
            count = failCount,
            selected = selectedFilter == CompleteFilter.FAIL,
            onClick = {
                onFilterSelected(CompleteFilter.FAIL)
            }
        )
    }
}

@Composable
private fun CompleteFilterChip(
    text: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = if (selected) Persimmon else White,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Persimmon else DarkBrown40
        )
    ) {
        Text(
            text = "$text $count",
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) White else BlackBrown
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CompleteFilterRowPreview() {
    OnulDo_FETheme {
        CompleteFilterRow(
            selectedFilter = CompleteFilter.ALL,
            allCount = 10,
            successCount = 7,
            failCount = 3,
            onFilterSelected = {}
        )
    }
}
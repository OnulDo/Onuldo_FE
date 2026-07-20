package com.example.onuldo_fe.ui.screen.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.onuldo_fe.ui.screen.record.component.RecordTabRow
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun RecordScreen() {

    var selectedTab by remember {
        mutableStateOf(RecordTab.PROGRESS)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp,start = 20.dp,end = 20.dp)
    ) {
        Text(
            text = "기록",
            color = BlackBrown,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "내 챌린지 기록을 확인하세요",
            color = DarkBrown,
            style = MaterialTheme.typography.labelMedium
        )

        RecordTabRow(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecordScreenPreview() {
    OnulDo_FETheme {
        RecordScreen()
    }
}
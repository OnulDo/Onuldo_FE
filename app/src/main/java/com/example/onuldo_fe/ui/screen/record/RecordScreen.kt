package com.example.onuldo_fe.ui.screen.record

import android.R.attr.spacing
import androidx.compose.foundation.layout.Box
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
import com.example.onuldo_fe.ui.screen.record.component.EmptyRecordView
import com.example.onuldo_fe.ui.screen.record.component.RecordTabRow
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun RecordScreen() {
    val spacing = LocalSpacing.current
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

        Spacer(modifier = Modifier.height(spacing.spacing8))

        Text(
            text = "내 챌린지 기록을 확인하세요",
            color = DarkBrown,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(spacing.spacing24))

        RecordTabRow(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
            }
        )

        when (selectedTab) {
            RecordTab.PROGRESS -> {
                EmptyRecordView(
                    title = "진행 중인 챌린지가 없어요",
                    description = "탐색 탭에서 새로운 챌린지를\n" + "찾아 시작해보세요!"
                )
            }

            RecordTab.COMPLETE -> {
                EmptyRecordView(
                    title = "완료한 챌린지가 없어요",
                    description = "첫 챌린지를 완주하고\n" + "성취 기록을 쌓아보세요!"
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecordScreenPreview() {
    OnulDo_FETheme {
        RecordScreen()
    }
}
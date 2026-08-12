package com.example.onuldo_fe.ui.screen.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.onuldo_fe.ui.component.RefreshOnResume
import com.example.onuldo_fe.viewmodel.record.RecordViewModel

@Composable
fun RecordRoute(
    onBrowseChallenges: () -> Unit,
    // 기기 푸시 탭 랜딩(종료일 리마인더→진행중, 환급 완료→완료)에서 초기 탭을 지정
    initialTab: RecordTab = RecordTab.PROGRESS,
    viewModel: RecordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RefreshOnResume(onResume = viewModel::refreshRecords)

    RecordScreen(
        progressList = uiState.ongoingRecords,
        completeList = uiState.completedRecords,
        totalCompletedCount = uiState.totalCompletedCount,
        successRate = uiState.successRate,
        totalSavedAmount = uiState.totalSavedAmount,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onRetry = viewModel::loadRecords,
        onBrowseChallenges = onBrowseChallenges,
        initialTab = initialTab
    )
}

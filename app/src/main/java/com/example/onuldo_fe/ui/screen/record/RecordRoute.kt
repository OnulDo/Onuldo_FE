package com.example.onuldo_fe.ui.screen.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.onuldo_fe.viewmodel.record.RecordViewModel

@Composable
fun RecordRoute(
    onBrowseChallenges: () -> Unit,
    viewModel: RecordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    RecordScreen(
        progressList = uiState.ongoingRecords,
        completeList = uiState.completedRecords,
        totalCompletedCount = uiState.totalCompletedCount,
        successRate = uiState.successRate,
        totalSavedAmount = uiState.totalSavedAmount,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onRetry = viewModel::loadRecords,
        onBrowseChallenges = onBrowseChallenges
    )
}
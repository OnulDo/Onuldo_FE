package com.example.onuldo_fe.ui.screen.challenge.participate

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.viewmodel.challenge.ParticipateViewModel

/**
 * 챌린지 참여 화면의 상태 보유 진입점.
 * challengeId로 참여(POST participations)를 처리하고, 성공 시 [onSuccess]로 완료 화면 이동.
 */
@Composable
fun ParticipateRoute(
    challengeId: Long,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    onChargePoint: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParticipateViewModel = viewModel(
        factory = ParticipateViewModel.factory(challengeId)
    )
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    // 참여 성공 시 완료 화면으로 이동
    LaunchedEffect(uiState.result) {
        if (uiState.result != null) onSuccess()
    }

    // 참여 실패 시 토스트 안내
    LaunchedEffect(uiState.isError) {
        if (uiState.isError) {
            Toast.makeText(context, "참여에 실패했어요. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            viewModel.onErrorShown()
        }
    }

    ParticipateScreen(
        isSubmitting = uiState.isSubmitting,
        // 서버가 포인트 부족(INSUFFICIENT_POINT)을 주면 충전 팝업 노출
        showInsufficientDialog = uiState.isInsufficientPoint,
        onDismissInsufficient = viewModel::onInsufficientDismissed,
        onBackClick = onBackClick,
        onStartClick = { durationWeeks, depositAmount ->
            viewModel.participate(durationWeeks = durationWeeks, depositAmount = depositAmount)
        },
        onChargePoint = onChargePoint,
        modifier = modifier
    )
}

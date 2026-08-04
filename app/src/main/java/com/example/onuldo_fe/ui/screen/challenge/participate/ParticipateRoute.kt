package com.example.onuldo_fe.ui.screen.challenge.participate

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge
import com.example.onuldo_fe.viewmodel.challenge.ParticipateViewModel

/**
 * 챌린지 참여 화면의 상태 보유 진입점.
 * challengeId로 참여(POST participations)를 처리하고, 성공 시 [onSuccess]로 완료 화면 이동.
 */
@Composable
fun ParticipateRoute(
    challengeId: Long,
    // 상세 화면에서 넘겨받은 정보박스 값(제목/한줄설명/카테고리/인증시간). 참여 화면에선 재조회하지 않는다.
    title: String,
    description: String,
    category: String,
    timeStart: String,
    timeEnd: String,
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
        // 정보박스 값은 상세에서 넘겨받은 그대로 사용 (제목만 Challenge로 감싸 전달)
        challenge = Challenge(id = challengeId.toInt(), title = title, participantCount = 0),
        category = category,
        description = description,
        timeStart = timeStart,
        timeEnd = timeEnd,
        isSubmitting = uiState.isSubmitting,
        // 서버가 포인트 부족(INSUFFICIENT_POINT)을 주면 충전 팝업 노출
        showInsufficientDialog = uiState.isInsufficientPoint,
        onDismissInsufficient = viewModel::onInsufficientDismissed,
        // 지갑 잔액(현재 더미) — 포인트 부족 팝업의 "보유 포인트"로 사용 //TODO: 포인트 충전? UI 나오면 변경
        ownedPoint = uiState.balance,
        onBackClick = onBackClick,
        onStartClick = { durationWeeks, depositAmount ->
            viewModel.participate(durationWeeks = durationWeeks, depositAmount = depositAmount)
        },
        onChargePoint = onChargePoint,
        modifier = modifier
    )
}

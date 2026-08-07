package com.example.onuldo_fe.ui.screen.challenge.participate

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge
import com.example.onuldo_fe.viewmodel.challenge.ParticipateViewModel

/**
 * 챌린지 참여 화면의 상태 보유 진입점.
 * challengeId로 참여(POST participations)를 처리하고, 성공 시 같은 Route 안에서 완료 화면(StartDone)을 렌더한다.
 * (StartDone은 참여 플로우의 마지막 상태라 별도 목적지로 두지 않고 result로 전환 — 네비 인자 전달 없이 재사용)
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
    onHome: () -> Unit,   // 완료 화면 "홈으로 가기"
    onChargePoint: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParticipateViewModel = viewModel(
        factory = ParticipateViewModel.factory(challengeId)
    )
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // 충전 화면으로 이동한 경우에만 복귀 시 지갑을 재조회한다(앱 전환·회전 등 일반 복귀엔 불필요한 재조회 방지).
    var shouldRefreshWallet by rememberSaveable { mutableStateOf(false) }

    // 충전 후 뒤로가기로 돌아왔을 때만 지갑 잔액을 다시 조회해 최신 보유 포인트를 반영한다.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && shouldRefreshWallet) {
                viewModel.refreshWallet()
                shouldRefreshWallet = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 참여 실패 시 토스트 안내 // TODO:  챌린지 토스트 추가예정
    LaunchedEffect(uiState.isError) {
        if (uiState.isError) {
            Toast.makeText(context, "참여에 실패했어요. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            viewModel.onErrorShown()
        }
    }

    // 이미 참여중이면 오류가 아니라 안내 후 상세로 복귀 (참여 화면에 머물지 않음) // TODO:  챌린지 토스트 추가예정
    LaunchedEffect(uiState.isAlreadyParticipating) {
        if (uiState.isAlreadyParticipating) {
            Toast.makeText(context, "이미 참여중인 챌린지입니다", Toast.LENGTH_SHORT).show()
            viewModel.onAlreadyParticipatingShown()
            onBackClick()
        }
    }

    val result = uiState.result
    if (result != null) {
        // 참여 성공 — 완료 화면- 응답(result)
        StartDoneScreen(
            result = result,
            title = title,
            category = category,
            timeStart = timeStart,
            timeEnd = timeEnd,
            onHomeClick = onHome,
            modifier = modifier
        )
    } else {
        ParticipateScreen(
            // 정보박스 값은 상세에서 넘겨받은 그대로 사용 (제목만 Challenge로 감싸 전달)
            challenge = Challenge(id = challengeId, title = title, participantCount = 0),
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
            onChargePoint = {
                // 충전 화면으로 이동하는 경우만 표시 → 복귀 시 지갑 재조회
                shouldRefreshWallet = true
                onChargePoint()
            },
            modifier = modifier
        )
    }
}

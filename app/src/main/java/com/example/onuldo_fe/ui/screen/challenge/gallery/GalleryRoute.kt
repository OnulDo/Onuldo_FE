package com.example.onuldo_fe.ui.screen.challenge.gallery

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.viewmodel.challenge.ChallengeListViewModel

@Composable
fun GalleryRoute(
    onChallengeClick: (Challenge) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengeListViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState = viewModel.uiState

    // 최초 로드가 끝난 뒤 이 화면으로 되돌아오면, 로딩/새로고침 표시 없이 조용히 최신 목록으로 갱신
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && viewModel.uiState.hasLoaded) {
                viewModel.silentRefresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 조회 실패는 토스트로 안내 (서버/네트워크 문구 그대로, 목록은 빈 상태 유지)
    // 재시도는 새로고침(pull-to-refresh)
    LaunchedEffect(uiState.isError) {
        if (uiState.isError) {
            Toast.makeText(
                context,
                uiState.errorMessage ?: "챌린지 목록을 불러오지 못했어요",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.onErrorShown()
        }
    }

    GalleryScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onChallengeClick = onChallengeClick,
        onRefresh = viewModel::refresh,
        modifier = modifier
    )
}
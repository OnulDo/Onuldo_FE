package com.example.onuldo_fe.ui.screen.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.component.PermissionDialogType
import com.example.onuldo_fe.ui.component.PermissionSettingDialog
import com.example.onuldo_fe.util.moveToAppSettings
import com.example.onuldo_fe.viewmodel.home.HomeViewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = viewModel(),
    onSettlementResultClick: (Long) -> Unit = {},
    onBrowseChallengesClick: () -> Unit = {},
    onCameraNavigate: () -> Unit = {},
    refreshKey: Int = 0
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showNotification by rememberSaveable { mutableStateOf(false) }
    var showCameraPermissionDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(refreshKey) {
        if (refreshKey > 0) {
            // 파티 시작 후 홈 복귀 시 최신 참여 파티를 다시 조회
            viewModel.loadHome()
        }
    }

    fun handleVerifyClick() {
        val isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (isCameraPermissionGranted) {
            onCameraNavigate()
        } else {
            showCameraPermissionDialog = true
        }
    }

    DisposableEffect(lifecycleOwner, showCameraPermissionDialog) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && showCameraPermissionDialog) {
                val isCameraPermissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (isCameraPermissionGranted) {
                    showCameraPermissionDialog = false
                    onCameraNavigate()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // 최초 로드가 끝난 뒤 홈으로 돌아오는 경우에만 최신 데이터를 조회한다.
            if (event == Lifecycle.Event.ON_RESUME && viewModel.uiState.hasLoadedHome) {
                viewModel.refreshHome()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (showNotification) {
        BackHandler { showNotification = false }
        NotificationRoute(onBackClick = { showNotification = false })
    } else {
        HomeScreen(
            uiState = viewModel.uiState,
            onNotificationClick = { showNotification = true },
            onSettlementResultClick = { partyId ->
                // 결과 화면 이동을 요청한 뒤 현재 홈 세션에서 확인한 배너 제거
                onSettlementResultClick(partyId)
                viewModel.confirmSettlementResult()
            },
            onBrowseChallengesClick = onBrowseChallengesClick,
            onVerifyClick = ::handleVerifyClick,
            onRefresh = viewModel::refreshHome,
            scrollToTopKey = refreshKey
        )
    }

    if (showCameraPermissionDialog) {
        PermissionSettingDialog(
            type = PermissionDialogType.CAMERA,
            onDismiss = { showCameraPermissionDialog = false },
            onMoveToSettings = { moveToAppSettings(context) }
        )
    }
}

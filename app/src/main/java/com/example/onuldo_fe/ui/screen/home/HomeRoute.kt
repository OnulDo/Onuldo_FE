package com.example.onuldo_fe.ui.screen.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.data.notification.NotificationPermissionPrefs
import com.example.onuldo_fe.model.home.notification.NotificationLandingBus
import com.example.onuldo_fe.model.home.notification.toLanding
import com.example.onuldo_fe.ui.component.PermissionDialogType
import com.example.onuldo_fe.ui.component.PermissionSettingDialog
import com.example.onuldo_fe.util.moveToAppSettings
import com.example.onuldo_fe.viewmodel.home.HomeViewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = viewModel(),
    onSettlementResultClick: (Long) -> Unit = {},
    onBrowseChallengesClick: () -> Unit = {},
    onCameraNavigate: (Long, String, String, String) -> Unit = { _, _, _, _ -> },
    refreshKey: Int = 0
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showNotification by rememberSaveable { mutableStateOf(false) }
    var showCameraPermissionDialog by rememberSaveable { mutableStateOf(false) }
    var showNotificationPermissionDialog by rememberSaveable { mutableStateOf(false) }

    // 홈 최초 진입 시(기기별 1회) 알림 권한이 없으면 커스텀 안내 팝업
    // 종 클릭과는 무관
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !NotificationPermissionPrefs.isRequested(context)
        ) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                NotificationPermissionPrefs.markRequested(context)
                showNotificationPermissionDialog = true
            }
        }
    }
    var pendingChallengeId by rememberSaveable { mutableStateOf<Long?>(null) }
    var pendingCategory by rememberSaveable { mutableStateOf("") }
    var pendingTitle by rememberSaveable { mutableStateOf("") }
    var pendingDeadline by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(refreshKey) {
        if (refreshKey > 0) {
            // 파티 시작 후 홈 복귀 시 최신 참여 파티를 다시 조회
            viewModel.loadHome()
        }
    }

        //권한x -> 알림 페이지는 열람 가능
    fun handleNotificationClick() {
        showNotification = true
    }

    fun handleVerifyClick(challengeId: Long, category: String, title: String, deadline: String) {
        if (challengeId <= 0L) return
        pendingChallengeId = challengeId
        pendingCategory = category
        pendingTitle = title
        pendingDeadline = deadline
        val isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (isCameraPermissionGranted) {
            onCameraNavigate(challengeId, category, title, deadline)
            pendingChallengeId = null
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
                    pendingChallengeId?.let { challengeId ->
                        onCameraNavigate(challengeId, pendingCategory, pendingTitle, pendingDeadline)
                    }
                    pendingChallengeId = null
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
        NotificationRoute(
            onBackClick = { showNotification = false },
            onItemClick = { item ->
                showNotification = false
                NotificationLandingBus.post(item.toLanding())
            }
        )
    } else {
        HomeScreen(
            uiState = viewModel.uiState,
            onNotificationClick = ::handleNotificationClick,
            // 정산 결과 API가 확인 처리하고, 홈 복귀 시 ON_RESUME 재조회로 배너가 사라진다.
            onSettlementResultClick = onSettlementResultClick,
            onBrowseChallengesClick = onBrowseChallengesClick,
            onVerifyClick = ::handleVerifyClick,
            onRefresh = viewModel::refreshHome,
            onRetry = viewModel::loadHome,
            scrollToTopKey = refreshKey
        )
    }

    // 카메라 권한 안내 팝업 → "설정으로 이동"이면 앱 설정으로
    if (showCameraPermissionDialog) {
        PermissionSettingDialog(
            type = PermissionDialogType.CAMERA,
            onDismiss = {
                showCameraPermissionDialog = false
                pendingChallengeId = null
                pendingCategory = ""
                pendingTitle = ""
                pendingDeadline = ""
            },
            onMoveToSettings = { moveToAppSettings(context) }
        )
    }

    // 홈 진입 시 알림 권한 안내 팝업 → "설정으로 이동" 시 시스템 앱 알림설정으로
    if (showNotificationPermissionDialog) {
        PermissionSettingDialog(
            type = PermissionDialogType.NOTIFICATION,
            onDismiss = { showNotificationPermissionDialog = false },
            onMoveToSettings = {
                showNotificationPermissionDialog = false
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
        )
    }
}

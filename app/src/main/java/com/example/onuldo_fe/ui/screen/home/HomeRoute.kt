package com.example.onuldo_fe.ui.screen.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.component.PermissionDialogType
import com.example.onuldo_fe.ui.component.PermissionSettingDialog
import com.example.onuldo_fe.viewmodel.home.HomeViewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = viewModel(),
    onSettlementResultClick: (String) -> Unit = {},
    onCameraPermissionRequired: () -> Unit = {},
    onCameraNavigate: () -> Unit = {}
) {
    val context = LocalContext.current
    var showNotification by rememberSaveable { mutableStateOf(false) }
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }

    fun handleNotificationClick() {
        // 알림 권한 있으면 알림 화면, 없으면 권한 안내 팝업 (API 33 미만은 런타임 권한 없음 → 바로 진입)
        val isNotificationPermissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        if (isNotificationPermissionGranted) {
            showNotification = true
        } else {
            showNotificationPermissionDialog = true
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
            onCameraPermissionRequired()
        }
    }

    if (showNotification) {
        BackHandler { showNotification = false }
        NotificationRoute(onBackClick = { showNotification = false })
    } else {
        HomeScreen(
            uiState = viewModel.uiState,
            onNotificationClick = ::handleNotificationClick,
            onSettlementResultClick = onSettlementResultClick,
            onVerifyClick = ::handleVerifyClick
        )

        // 알림 권한 없을 때(최초) 안내 팝업 → "설정으로 이동"이면 시스템(폰) 설정으로
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
}

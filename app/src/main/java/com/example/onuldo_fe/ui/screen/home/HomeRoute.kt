package com.example.onuldo_fe.ui.screen.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
    onSettlementResultClick: (String) -> Unit = {},
    onCameraNavigate: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showNotification by rememberSaveable { mutableStateOf(false) }
    var showCameraPermissionDialog by rememberSaveable { mutableStateOf(false) }

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

    if (showNotification) {
        BackHandler { showNotification = false }
        NotificationRoute(onBackClick = { showNotification = false })
    } else {
        HomeScreen(
            uiState = viewModel.uiState,
            onNotificationClick = { showNotification = true },
            onSettlementResultClick = onSettlementResultClick,
            onVerifyClick = ::handleVerifyClick
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

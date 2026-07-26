package com.example.onuldo_fe.ui.screen.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
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
            onNotificationClick = { showNotification = true },
            onSettlementResultClick = onSettlementResultClick,
            onVerifyClick = ::handleVerifyClick
        )
    }
}

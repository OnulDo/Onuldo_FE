package com.example.onuldo_fe.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
fun CameraPermissionScreen(
    onBack: () -> Unit,
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && isCameraPermissionGranted()) {
                onPermissionGranted()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler(onBack = onBack)

    CameraPermissionDialog(
        onDismiss = onBack,
        onMoveToSettings = {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
        }
    )
}

@Composable
fun CameraPermissionDialog(
    onDismiss: () -> Unit,
    onMoveToSettings: () -> Unit,
) {
    val spacing = LocalSpacing.current

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier.width(280.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Persimmon20),
            color = SourCream
        ) {
            Column {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = spacing.spacing28,
                            vertical = spacing.spacing28
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "카메라 권한이 필요해요",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BlackBrown
                    )

                    Spacer(modifier = Modifier.height(spacing.spacing12))

                    Text(
                        text = "챌린지 인증을 위해 카메라 권한이 필요해요. 설정에서 허용해주세요.",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkBrown50,
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(
                    color = DarkBrown20
                )

                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "취소",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkBrown50
                        )
                    }

                    VerticalDivider(
                        color = Persimmon20
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(onClick = onMoveToSettings),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "설정으로 이동",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Persimmon
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CameraPermissionDialogPreview() {
    OnulDo_FETheme {
        CameraPermissionDialog(
            onDismiss = {},
            onMoveToSettings = {}
        )
    }
}

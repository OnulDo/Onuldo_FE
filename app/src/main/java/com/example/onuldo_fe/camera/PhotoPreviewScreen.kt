package com.example.onuldo_fe.camera

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.onuldo_fe.camera.component.CameraTopBar
import com.example.onuldo_fe.camera.component.PreviewBottomBar
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun PhotoPreviewScreen(
    category: String,
    title: String,
    imageUri: Uri?,
    onCloseClick: () -> Unit = {},
    onRetakeClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    submitState: VerificationSubmitState = VerificationSubmitState.Idle,
    onRetry: () -> Unit = {},
    onUploadComplete: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        val cameraHeight = maxWidth * 4f / 3f
        val cameraTop = (maxHeight - cameraHeight) / 2f
        val cameraBottom = cameraTop + cameraHeight
        val bottomAreaHeight = (maxHeight - cameraBottom).coerceAtLeast(0.dp)

        AsyncImage(
            model = imageUri,
            contentDescription = "촬영한 사진",
            modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f).align(Alignment.Center),
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier.fillMaxWidth().height(cameraTop.coerceAtLeast(0.dp))
                .align(Alignment.TopCenter).statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            CameraTopBar(
                category = category,
                title = title,
                showFlashButton = false,
                onCloseClick = onCloseClick
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(bottomAreaHeight)
                .align(Alignment.BottomCenter).navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            PreviewBottomBar(
                onRetakeClick = onRetakeClick,
                onSubmitClick = onSubmitClick,
                enabled = submitState != VerificationSubmitState.Loading
            )
        }

        if (submitState == VerificationSubmitState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    when (submitState) {
        is VerificationSubmitState.Error -> AlertDialog(
            onDismissRequest = {},
            title = { Text("업로드 실패") },
            text = { Text(submitState.message) },
            confirmButton = { Button(onClick = onRetry) { Text("재시도") } },
            dismissButton = { Button(onClick = onRetakeClick) { Text("다시 촬영") } }
        )
        is VerificationSubmitState.Success -> AlertDialog(
            onDismissRequest = {},
            title = { Text("업로드 완료") },
            text = { Text("사진이 안전하게 업로드되었습니다.") },
            confirmButton = { Button(onClick = onUploadComplete) { Text("확인") } }
        )
        else -> Unit
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoPreviewScreenPreview() {
    OnulDo_FETheme {
        PhotoPreviewScreen(category = "시간 챌린지", title = "30분 휴식", imageUri = null)
    }
}
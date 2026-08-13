package com.example.onuldo_fe.ui.screen.camera

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.onuldo_fe.ui.screen.camera.component.CameraTopBar
import com.example.onuldo_fe.ui.screen.camera.component.PreviewBottomBar
import com.example.onuldo_fe.ui.component.OnulDoErrorDialog
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.viewmodel.verification.VerificationSubmitState

@Composable
fun PhotoPreviewScreen(
    category: String,
    title: String,
    imageUri: Uri?,
    onCloseClick: () -> Unit = {},
    onRetakeClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    submitState: VerificationSubmitState = VerificationSubmitState.Idle,
    onErrorConfirm: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        val layoutDimensions = calculateCameraLayoutDimensions(
            maxWidth = this.maxWidth,
            maxHeight = this.maxHeight
        )

        AsyncImage(
            model = imageUri,
            contentDescription = "촬영한 사진",
            modifier = Modifier
                .height(layoutDimensions.previewHeight)
                .aspectRatio(3f / 4f)
                .align(Alignment.Center),
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier.fillMaxWidth().height(layoutDimensions.topAreaHeight)
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
            modifier = Modifier.fillMaxWidth().height(layoutDimensions.bottomAreaHeight)
                .align(Alignment.BottomCenter).navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            PreviewBottomBar(
                onRetakeClick = onRetakeClick,
                onSubmitClick = onSubmitClick,
                enabled = submitState != VerificationSubmitState.Uploading &&
                    submitState != VerificationSubmitState.Reviewing
            )
        }

        if (submitState == VerificationSubmitState.Uploading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (submitState is VerificationSubmitState.Error) {
        OnulDoErrorDialog(
            title = "업로드에 실패했어요",
            description = submitState.message,
            buttonText = "확인",
            onButtonClick = onErrorConfirm,
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoPreviewScreenPreview() {
    OnulDo_FETheme {
        PhotoPreviewScreen(category = "시간 챌린지", title = "30분 휴식", imageUri = null)
    }
}

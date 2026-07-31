package com.example.onuldo_fe.camera

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.onuldo_fe.R
import com.example.onuldo_fe.camera.component.CameraTopBar
import com.example.onuldo_fe.camera.component.PreviewBottomBar
import com.example.onuldo_fe.navigation.Routes
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun PhotoPreviewScreen(
    category: String,
    title: String,
    imageUri: Uri?,
    onCloseClick: () -> Unit = {},
    onRetakeClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        AsyncImage(
            model = imageUri,
            contentDescription = "촬영한 사진",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f/4f)
                .align(Alignment.Center),
            contentScale = ContentScale.Fit
        )

        CameraTopBar(
            category = category,
            title = title,
            showFlashButton = false,
            onCloseClick = onCloseClick
        )

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            PreviewBottomBar(
                onRetakeClick = onRetakeClick,
                onSubmitClick = onSubmitClick

            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun PhotoPreviewScreenPreview() {
    OnulDo_FETheme {
        PhotoPreviewScreen(
            category = "외국어",
            title = "영단어 100개 암기",
            imageUri = null
        )
    }
}

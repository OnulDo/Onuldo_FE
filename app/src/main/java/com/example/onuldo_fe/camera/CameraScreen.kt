package com.example.onuldo_fe.camera

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.onuldo_fe.camera.component.CameraBottomBar
import com.example.onuldo_fe.camera.component.CameraPreview
import com.example.onuldo_fe.camera.component.CameraTopBar
import com.example.onuldo_fe.ui.screen.challenge.detail.component.VerificationNoticeBottomSheet
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun CameraScreen(
    category: String,
    title: String,
    onPhotoCaptured: (Uri?) -> Unit
) {
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }
    var showVerificationNotice by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 카메라 프리뷰
         CameraPreview(
            imageCapture = imageCapture,
            modifier = Modifier.fillMaxSize()
        )
       /* Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )*/

        CameraTopBar(
            category = category,
            title = title,
            showFlashButton = true,
            onCloseClick = {},
            onFlashClick = {  }
        )
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val context = LocalContext.current
            CameraBottomBar(
                onNoteClick = {
                    showVerificationNotice = true
                },
                onCaptureClick = {
                    val name = System.currentTimeMillis().toString()
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(
                                MediaStore.Images.Media.RELATIVE_PATH,
                                "Pictures/OnulDo"
                            )
                        }
                    }
                    val outputOptions =
                        ImageCapture.OutputFileOptions.Builder(
                            context.contentResolver,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        ).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {

                            override fun onImageSaved(
                                outputFileResults: ImageCapture.OutputFileResults
                            ) {
                                val savedUri = outputFileResults.savedUri
                                onPhotoCaptured(savedUri)
                            }

                            override fun onError(
                                exception: ImageCaptureException
                            ) {
                                exception.printStackTrace()
                            }
                        }
                    )
                }
            )
        }

        if (showVerificationNotice) {
            VerificationNoticeBottomSheet(
                challengeTitle = title,
                onDismiss = { showVerificationNotice = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CameraScreenPreview() {
    OnulDo_FETheme {
        CameraScreen(
            category = "외국어",
            title = "영단어 100개 암기",
            onPhotoCaptured = {}
        )
    }
}

package com.example.onuldo_fe.camera

import android.net.Uri
import android.util.Size
import java.io.File
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    onPhotoCaptured: (Uri?) -> Unit,
    onCloseClick: () -> Unit
){
    val imageCapture = remember {
        ImageCapture.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            Size(1920, 1440),
                            ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER_THEN_HIGHER
                        )
                    )
                    .build()
            )
            .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .build()
    }
    var showVerificationNotice by remember { mutableStateOf(false) }

    var lensFacing by rememberSaveable {
        mutableStateOf(CameraSelector.LENS_FACING_BACK)
    }

    var flashMode by rememberSaveable {
        mutableStateOf(ImageCapture.FLASH_MODE_OFF)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val cameraHeight = maxWidth * 4f / 3f
        val cameraTop = (maxHeight - cameraHeight) / 2f
        val cameraBottom = cameraTop + cameraHeight
        val bottomAreaHeight = (maxHeight - cameraBottom).coerceAtLeast(0.dp)

        // 카메라 프리뷰
        key(lensFacing) {
            CameraPreview(
                imageCapture = imageCapture,
                lensFacing = lensFacing,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .align(Alignment.Center)
            )
        }
       /* Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )*/

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cameraTop.coerceAtLeast(0.dp))
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            CameraTopBar(
                category = category,
                title = title,
                showFlashButton =
                    lensFacing == CameraSelector.LENS_FACING_BACK,
                onCloseClick = onCloseClick,
                onFlashClick = {
                    val newFlashMode =
                        if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                            ImageCapture.FLASH_MODE_ON
                        } else {
                            ImageCapture.FLASH_MODE_OFF
                        }

                    flashMode = newFlashMode
                    imageCapture.flashMode = newFlashMode
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomAreaHeight)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            val context = LocalContext.current
            CameraBottomBar(
                onNoteClick = {
                    showVerificationNotice = true
                },
                onCaptureClick = {
                    val photoDirectory = File(context.cacheDir, "verification_photos").apply { mkdirs() }
                    val photoFile = File(photoDirectory, "verification_${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {

                            override fun onImageSaved(
                                outputFileResults: ImageCapture.OutputFileResults
                            ) {
                                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                                onPhotoCaptured(savedUri)
                            }

                            override fun onError(
                                exception: ImageCaptureException
                            ) {
                                photoFile.delete()
                                exception.printStackTrace()
                            }
                        }
                    )
                },
                onSwitchClick = {
                    lensFacing =
                        if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }

                    flashMode = ImageCapture.FLASH_MODE_OFF
                    imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
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
            onPhotoCaptured = {},
            onCloseClick = {}
        )
    }
}

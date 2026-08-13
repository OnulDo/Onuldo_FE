package com.example.onuldo_fe.ui.screen.camera

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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.content.ContextCompat
import com.example.onuldo_fe.ui.screen.camera.component.CameraBottomBar
import com.example.onuldo_fe.ui.screen.camera.component.CameraPreview
import com.example.onuldo_fe.ui.screen.camera.component.CameraTopBar
import com.example.onuldo_fe.ui.screen.challenge.detail.component.VerificationNoticeBottomSheet
import com.example.onuldo_fe.ui.component.OnulDoErrorDialog
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun CameraScreen(
    category: String,
    title: String,
    successConditions: List<String> = emptyList(),
    failureConditions: List<String> = emptyList(),
    isNoticeLoading: Boolean = false,
    isNoticeError: Boolean = false,
    onNoticeRetry: () -> Unit = {},
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
    var showNoticeError by remember { mutableStateOf(false) }
    var openNoticeAfterLoading by remember { mutableStateOf(false) }

    var lensFacing by rememberSaveable {
        mutableStateOf(CameraSelector.LENS_FACING_BACK)
    }

    var flashMode by rememberSaveable {
        mutableStateOf(ImageCapture.FLASH_MODE_OFF)
    }

    LaunchedEffect(imageCapture, flashMode) {
        imageCapture.flashMode = flashMode
    }

    LaunchedEffect(isNoticeLoading, isNoticeError) {
        if (openNoticeAfterLoading && !isNoticeLoading) {
            openNoticeAfterLoading = false
            if (isNoticeError) showNoticeError = true else showVerificationNotice = true
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val layoutDimensions = calculateCameraLayoutDimensions(
            maxWidth = this.maxWidth,
            maxHeight = this.maxHeight
        )

        // 카메라 프리뷰
        key(lensFacing) {
            CameraPreview(
                imageCapture = imageCapture,
                lensFacing = lensFacing,
                modifier = Modifier
                    .height(layoutDimensions.previewHeight)
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
                .height(layoutDimensions.topAreaHeight)
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            CameraTopBar(
                category = category,
                title = title,
                showFlashButton =
                    lensFacing == CameraSelector.LENS_FACING_BACK,
                isFlashOn = flashMode == ImageCapture.FLASH_MODE_ON,
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
                .height(layoutDimensions.bottomAreaHeight)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            val context = LocalContext.current
            CameraBottomBar(
                onNoteClick = {
                    when {
                        isNoticeLoading -> openNoticeAfterLoading = true
                        isNoticeError -> showNoticeError = true
                        else -> showVerificationNotice = true
                    }
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
                successConditions = successConditions,
                failureConditions = failureConditions,
                onDismiss = { showVerificationNotice = false }
            )
        }

        if (showNoticeError) {
            OnulDoErrorDialog(
                title = "유의사항을 불러오지 못했어요",
                description = "인터넷 연결을 확인한 후 다시 시도해 주세요.",
                buttonText = "재시도",
                onButtonClick = {
                    showNoticeError = false
                    openNoticeAfterLoading = true
                    onNoticeRetry()
                },
                onDismiss = { showNoticeError = false }
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

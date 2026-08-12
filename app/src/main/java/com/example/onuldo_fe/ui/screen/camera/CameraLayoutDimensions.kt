package com.example.onuldo_fe.ui.screen.camera

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class CameraLayoutDimensions(
    val previewHeight: Dp,
    val topAreaHeight: Dp,
    val bottomAreaHeight: Dp
)

/**
 * 상·하단 촬영 컨트롤의 최소 공간을 먼저 확보하고, 남은 영역 안에서만
 * 3:4 프리뷰를 표시한다. 화면이 짧아져도 컨트롤 높이가 0dp가 되지 않는다.
 */
internal fun calculateCameraLayoutDimensions(
    maxWidth: Dp,
    maxHeight: Dp,
    minimumTopAreaHeight: Dp = 72.dp,
    minimumBottomAreaHeight: Dp = 112.dp
): CameraLayoutDimensions {
    val minimumControlHeight = minimumTopAreaHeight + minimumBottomAreaHeight
    val availablePreviewHeight = (maxHeight - minimumControlHeight).coerceAtLeast(0.dp)
    val previewHeight = minOf(maxWidth * 4f / 3f, availablePreviewHeight)
    val remainingHeight = (maxHeight - previewHeight).coerceAtLeast(0.dp)

    if (remainingHeight < minimumControlHeight) {
        return CameraLayoutDimensions(
            previewHeight = previewHeight,
            topAreaHeight = minimumTopAreaHeight,
            bottomAreaHeight = minimumBottomAreaHeight
        )
    }

    val balancedTopHeight = remainingHeight / 2f
    val maximumTopHeight = remainingHeight - minimumBottomAreaHeight
    val topAreaHeight = balancedTopHeight.coerceIn(
        minimumValue = minimumTopAreaHeight,
        maximumValue = maximumTopHeight
    )

    return CameraLayoutDimensions(
        previewHeight = previewHeight,
        topAreaHeight = topAreaHeight,
        bottomAreaHeight = remainingHeight - topAreaHeight
    )
}

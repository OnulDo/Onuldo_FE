package com.example.onuldo_fe.ui.screen.party.component

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage

/**
 * API 이미지 URL을 우선 표시하고 URL이 없거나 로드에 실패하면 로컬 이미지를 표시합니다.
 */
@Composable
fun PartyNetworkImage(
    imageUrl: String?,
    @DrawableRes fallbackImageRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val fallbackPainter = painterResource(fallbackImageRes)
    AsyncImage(
        model = imageUrl?.takeIf(String::isNotBlank) ?: fallbackImageRes,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = fallbackPainter,
        error = fallbackPainter,
        fallback = fallbackPainter
    )
}

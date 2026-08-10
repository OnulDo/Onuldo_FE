package com.example.onuldo_fe.ui.screen.party.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.onuldo_fe.ui.screen.login.ProfileCharacters
import com.example.onuldo_fe.utils.ProfileAsset

/**
 * API 이미지 URL을 우선 표시하고 URL이 없거나 로드에 실패하면 로컬 이미지 표시.
 */
@Composable
fun PartyNetworkImage(
    imageUrl: String?,
    @DrawableRes fallbackImageRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val presetRes = ProfileAsset.toCharacterIndex(imageUrl)?.let(ProfileCharacters::getOrNull)
    if (presetRes != null) {
        Image(
            painter = painterResource(presetRes),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
        return
    }

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

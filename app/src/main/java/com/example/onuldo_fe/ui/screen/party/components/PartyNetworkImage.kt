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

@Composable
fun PartyNetworkImage(
    imageUrl: String?,
    @DrawableRes fallbackImageRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    // 커스텀 업로드 URL(프리셋이 아닌 실제 사진)에 별도 스케일·크기가 필요할 때만 지정.
    // 기본값은 프리셋과 동일해 기존 호출부 동작을 그대로 유지한다.
    networkModifier: Modifier = modifier,
    networkContentScale: ContentScale = contentScale
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
        modifier = networkModifier,
        contentScale = networkContentScale,
        placeholder = fallbackPainter,
        error = fallbackPainter,
        fallback = fallbackPainter
    )
}

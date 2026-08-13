package com.example.onuldo_fe.ui.screen.party.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10

/**
 * 서버 프로필 이미지 URL을 우선 표시하고, URL이 없거나 로드에 실패하면 기본 캐릭터를 표시합니다.
 */
@Composable
fun PartyMemberProfileImage(
    profileImageUrl: String?,
    defaultCharacterId: Int?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    containerSize: Dp = 33.dp,
    characterWidth: Dp = 28.dp,
    characterHeight: Dp = 33.dp,
    showContainer: Boolean = true,
    showBorder: Boolean = true,
    dimmed: Boolean = false
) {
    val containerModifier = modifier
        .size(containerSize)
        .alpha(if (dimmed) 0.5f else 1f)
        .then(
            if (showContainer) {
                Modifier.background(Persimmon10, CircleShape)
            } else {
                Modifier.background(Color.Transparent)
            }
        )
        .then(
            if (showContainer && showBorder) {
                Modifier.border(1.dp, Persimmon, CircleShape)
            } else {
                Modifier
            }
        )

    Box(containerModifier, contentAlignment = Alignment.Center) {
        PartyNetworkImage(
            imageUrl = profileImageUrl,
            fallbackImageRes = partyCharacterDrawable(defaultCharacterId),
            contentDescription = contentDescription,
            modifier = Modifier.size(width = characterWidth, height = characterHeight),
            contentScale = ContentScale.Fit,
            // 프리셋은 Fit 일러스트 크기를 쓰고, 실제 업로드 사진은 원형 컨테이너를 꽉 채워 크롭한다.
            networkModifier = Modifier.fillMaxSize().clip(CircleShape),
            networkContentScale = ContentScale.Crop
        )
    }
}

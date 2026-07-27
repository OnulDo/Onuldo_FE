package com.example.onuldo_fe.ui.component.party

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10

/**
 * 사용자가 선택한 기본 캐릭터 ID를 파티 화면에서 동일한 규격으로 표시합니다.
 * profileImageUrl은 API 호환을 위해 전달받지만 캐릭터 선택 정책에 따라 화면에서는 사용하지 않습니다.
 */
@Suppress("UNUSED_PARAMETER")
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
            imageUrl = null,
            fallbackImageRes = partyCharacterDrawable(defaultCharacterId),
            contentDescription = contentDescription,
            modifier = Modifier.size(width = characterWidth, height = characterHeight),
            contentScale = ContentScale.Fit
        )
    }
}

package com.example.onuldo_fe.ui.screen.challenge.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.White

/**
 * 챌린지 흰 박스 (컨테이너). 여러 곳에서 높이만 달라서 컨테이너만 분리
 * 가로는 부모가 결정(fillMaxWidth), 내부 여백은 content 쪽에서 지정
 */
@Composable
fun ChallengeInfoBox(
    height: Dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp)),
        content = content
    )
}

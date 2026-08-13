package com.example.onuldo_fe.ui.screen.party.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

/**
 * 파티원 카드의 공통 레이아웃입니다.
 * 대기방 상태 배지와 정산 포인트처럼 화면마다 다른 오른쪽 내용만 외부에서 전달합니다.
 */
@Composable
fun PartyMemberCard(
    name: String,
    profileImageUrl: String?,
    defaultCharacterId: Int?,
    modifier: Modifier = Modifier,
    startPadding: Dp = 20.dp,
    endPadding: Dp = 20.dp,
    nameWidth: Dp? = null,
    middleContent: @Composable () -> Unit = {},
    trailingContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(White, RoundedCornerShape(12.dp))
            .border(1.dp, DarkBrown40, RoundedCornerShape(12.dp))
            .padding(start = startPadding, end = endPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PartyMemberProfileImage(
            profileImageUrl = profileImageUrl,
            defaultCharacterId = defaultCharacterId,
            contentDescription = "$name 프로필",
            containerSize = 40.dp,
            characterWidth = 28.dp,
            characterHeight = 33.dp
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = LocalSpacing.current.spacing12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                modifier = nameWidth?.let { Modifier.width(it) } ?: Modifier,
                color = BlackBrown,
                style = OnulDoTypography.caption2Bold,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
            middleContent()
        }
        trailingContent()
    }
}

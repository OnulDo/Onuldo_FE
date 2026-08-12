package com.example.onuldo_fe.ui.screen.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown20
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import androidx.compose.ui.text.font.FontWeight
import com.example.onuldo_fe.ui.theme.OnulDoTypography

@Composable
fun HomeVerifyButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    width: Dp,
    height: Dp,
    iconSize: Dp,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(height / 2)
    val actionColor = if (enabled) Persimmon else BlackBrown20
    val contentColor = when {
        !enabled -> BlackBrown20
        isPressed -> White
        else -> Persimmon
    }

    Row(
        modifier = modifier
            .size(width = width, height = height)
            .clip(shape)
            .background(if (enabled && isPressed) Persimmon else Color.Transparent)
            .border(BorderStroke(1.dp, actionColor), shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(
                if (enabled) R.drawable.home_camera_icon
                else R.drawable.home_camera_disabled_icon
            ),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            // 비활성 아이콘은 리소스에 지정된 색을 그대로 사용한다.
            tint = if (enabled) contentColor else Color.Unspecified
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = stringResource(R.string.home_challenge_action_verify),
            color = contentColor,
            style = OnulDoTypography.caption4Bold
        )
    }
}

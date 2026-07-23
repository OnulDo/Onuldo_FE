package com.example.onuldo_fe.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.White

private val TrackWidth = 44.dp
private val TrackHeight = 26.dp
private val ThumbSize = 22.dp
private val TrackPadding = 2.dp

/**
 * 오늘두 토글 스위치.
 *
 * 행 전체를 눌러 토글하는 경우 [onCheckedChange]에 null을 넘기고
 * 부모에 Modifier.toggleable을 붙인다.
 */
@Composable
fun OnulDoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) TrackWidth - ThumbSize - TrackPadding else TrackPadding,
        label = "thumbOffset"
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) Persimmon else DarkBrown20,
        label = "trackColor"
    )

    val toggleModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Switch,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onValueChange = onCheckedChange
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(width = TrackWidth, height = TrackHeight)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackColor)
            .then(toggleModifier),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(ThumbSize)
                .clip(CircleShape)
                .background(White)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnulDoSwitchPreview() {
    OnulDo_FETheme {
        var on by remember { mutableStateOf(true) }
        var off by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OnulDoSwitch(checked = off, onCheckedChange = { off = it })
            OnulDoSwitch(checked = on, onCheckedChange = { on = it })
            OnulDoSwitch(checked = false, onCheckedChange = {}, enabled = false)
        }
    }
}

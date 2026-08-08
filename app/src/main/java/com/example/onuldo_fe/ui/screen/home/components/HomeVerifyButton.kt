package com.example.onuldo_fe.ui.screen.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick as semanticsOnClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import androidx.compose.ui.text.font.FontWeight

@Composable
fun HomeVerifyButton(
    onClick: () -> Unit,
    width: Dp,
    height: Dp,
    iconSize: Dp,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(height / 2)
    val contentColor = if (isPressed) White else Persimmon

    Row(
        modifier = modifier
            .size(width = width, height = height)
            .clip(shape)
            .background(if (isPressed) Persimmon else Color.Transparent)
            .border(BorderStroke(1.dp, Persimmon), shape)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                semanticsOnClick(label = null) {
                    onClick()
                    true
                }
            }
            // 상위 verticalScroll/PullToRefreshBox가 짧은 탭의 미세한 손떨림을 스크롤 시도로
            // 오인해 제스처를 가로채면서 클릭이 씹히는 문제를 막기 위해, Initial 패스에서
            // 터치를 먼저 선점(consume)한다. 그래서 clickable 대신 탭을 직접 감지한다.
            .pointerInput(onClick) {
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    down.consume()
                    isPressed = true

                    val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    isPressed = false
                    if (up != null) {
                        up.consume()
                        onClick()
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.home_camera_icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = contentColor
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = stringResource(R.string.home_challenge_action_verify),
            color = contentColor,
            fontFamily = Pretendard,
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontWeight = FontWeight.Bold
        )
    }
}

package com.example.onuldo_fe.ui.screen.camera.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.White

@Composable
fun CameraBottomBar(
    modifier: Modifier = Modifier,
    onNoteClick: () -> Unit = {},
    onCaptureClick: () -> Unit = {},
    onSwitchClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing24),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //추후 촬영 버튼 외 기능 추가 예정
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onNoteClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.camera_note_btn),
                    contentDescription = "유의사항",
                    tint = White
                )
            }

            Text(
                text = "유의사항",
                style = MaterialTheme.typography.labelSmall,
                color = White
            )
        }

        IconButton(
            onClick = onCaptureClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.camera_shot_btn),
                contentDescription = "촬영",
                modifier = Modifier.fillMaxSize(),
                tint = Color.Unspecified
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onSwitchClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.camera_switch_btn),
                    contentDescription = "카메라 전환",
                    tint = White
                )
            }

            Text(
                text = "카메라 전환",
                style = MaterialTheme.typography.labelSmall,
                color = White
            )
        }
    }
}

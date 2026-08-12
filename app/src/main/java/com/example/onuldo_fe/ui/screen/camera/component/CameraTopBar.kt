package com.example.onuldo_fe.ui.screen.camera.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.SourCream70
import com.example.onuldo_fe.ui.theme.White

@Composable
fun CameraTopBar(
    category: String,
    title: String,
    modifier: Modifier = Modifier,
    showFlashButton: Boolean = true,
    isFlashOn: Boolean = false,
    onCloseClick: () -> Unit = {},
    onFlashClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = spacing.spacing8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onCloseClick
        ) {
            Icon(
                painter = painterResource(R.drawable.camera_close_btn),
                contentDescription = "닫기",
                tint = White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = category,
                style = OnulDoTypography.caption3Medium,
                color = SourCream70
            )

            Text(
                text = title,
                style = OnulDoTypography.body3Bold,
                color = White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (showFlashButton) {
            IconButton(
                onClick = onFlashClick
            ) {
                Icon(
                    painter = painterResource(
                        if (isFlashOn) R.drawable.camera_flash_on_btn
                        else R.drawable.camera_flash_btn
                    ),
                    contentDescription = "플래시",
                    tint = White
                )
            }
        } else {
            Spacer(
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

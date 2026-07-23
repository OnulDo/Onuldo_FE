package com.example.onuldo_fe.camera.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.SourCream70
import com.example.onuldo_fe.ui.theme.White

@Composable
fun CameraTopBar(
    category: String,
    title: String,
    onCloseClick: () -> Unit = {},
    onFlashClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = spacing.spacing20,
                vertical = spacing.spacing24
            ),
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
                style = MaterialTheme.typography.labelSmall,
                color = SourCream70
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onFlashClick
        ) {
            Icon(
                painter = painterResource(R.drawable.camera_flash_btn),
                contentDescription = "플래시",
                tint = White
            )
        }
    }
}
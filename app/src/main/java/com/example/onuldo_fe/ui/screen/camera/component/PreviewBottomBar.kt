package com.example.onuldo_fe.ui.screen.camera.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

@Composable
fun PreviewBottomBar(
    modifier: Modifier = Modifier,
    onRetakeClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    enabled: Boolean = true
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing20),
        horizontalArrangement = Arrangement.spacedBy(spacing.spacing12)
    ) {

        OutlinedButton(
            onClick = onRetakeClick,
            enabled = enabled,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "재촬영",
                style = OnulDoTypography.body5Bold,
                color = SourCream
            )
        }

        Button(
            onClick = onSubmitClick,
            enabled = enabled,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Persimmon
            )
        ) {
            Text(
                text = "인증 제출",
                style = OnulDoTypography.body5Bold,
                color = SourCream
            )
        }
    }
}

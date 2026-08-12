package com.example.onuldo_fe.ui.component

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
fun OnulDoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = OnulDoTypography.body3Bold,
    enabled: Boolean = true,
    height: Dp = 56.dp,
    horizontalPadding: Dp = 20.dp,
    containerColor: Color = Persimmon,
    contentColor: Color = SourCream,
    disabledContainerColor: Color = BlackBrown.copy(alpha = 0.1f),
    disabledContentColor: Color = BlackBrown.copy(alpha = 0.2f),
    pressedContainerColor: Color = DarkBrown,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .fillMaxWidth()
            .height(height),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) {
                pressedContainerColor
            } else {
                containerColor
            },
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        ),
        interactionSource = interactionSource,
    ) {
        Text(
            text = text,
            style = textStyle,
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun OnulDoButtonPreview() {
    OnulDo_FETheme {
        OnulDoButton(
            text = "클릭하세요",
            onClick = {},
        )
    }
}

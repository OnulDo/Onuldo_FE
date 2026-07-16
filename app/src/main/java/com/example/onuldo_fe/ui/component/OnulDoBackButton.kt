package com.example.onuldo_fe.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun OnulDoBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.back_btn),
            contentDescription = "뒤로 가기",
            modifier = Modifier.size(width = 8.dp, height = 14.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 120, heightDp = 120)
@Composable
private fun OnulDoBackButtonPreview() {
    OnulDo_FETheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            OnulDoBackButton()
        }
    }
}

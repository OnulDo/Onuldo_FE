package com.example.onuldo_fe.ui.screen.challenge.gallery.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.SourCream

// 필터 버튼 — 피그마 export 이미지 사용 (기본/눌림 두 상태). 인스턴트 전환.
@Composable
fun GalleryFilterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val active = selected || pressed

    Image(
        painter = painterResource(
            if (active) R.drawable.ic_challenge_filter_pressed
            else R.drawable.ic_challenge_filter_default
        ),
        contentDescription = "필터",
        modifier = modifier
            .size(27.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7)
@Composable
private fun GalleryFilterButtonPreview() {
    OnulDo_FETheme {
        Row(
            modifier = Modifier
                .background(SourCream)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GalleryFilterButton(onClick = {})
            GalleryFilterButton(onClick = {}, selected = true)
        }
    }
}

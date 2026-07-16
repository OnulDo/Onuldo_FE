package com.example.onuldo_fe.ui.screen.challenge.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream

//탐색 화면

@Composable
fun ChallengeSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "생활루틴" // 예시?
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val borderColor = if (focused) Persimmon.copy(alpha = 0.4f) else DarkBrown40
    val iconColor = if (focused) Persimmon else DarkBrown // 포커스 시 돋보기도 주황

    val textStyle = MaterialTheme.typography.labelLarge.copy(
        fontSize = 13.sp,
        lineHeight = 13.sp
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(27.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(top = 6.dp, bottom = 6.dp, start = 11.dp, end = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 돋보기 아이콘 (피그마 export, ic_search) — tint로 색 제어
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(11.dp)) // gap 11

        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle,
                    color = DarkBrown40
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                cursorBrush = SolidColor(Persimmon),
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 348)
@Composable
private fun ChallengeSearchBarPreview() {
    OnulDo_FETheme {
        var query by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .background(SourCream)
                .padding(20.dp)
        ) {
            ChallengeSearchBar(value = query, onValueChange = { query = it })
        }
    }
}

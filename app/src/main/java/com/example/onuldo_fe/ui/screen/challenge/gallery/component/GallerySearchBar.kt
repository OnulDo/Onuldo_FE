package com.example.onuldo_fe.ui.screen.challenge.gallery.component

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

//검색창 (특징: 검색 바를위해 제작)
@Composable
fun GallerySearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "생활루틴"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    // 포커스(입력 중)면 테두리 brand/brown/100(#5C2C03), 아니면 brown/40
    val borderColor = if (isFocused) DarkBrown  else DarkBrown40

    // Caption1
    val textStyle = OnulDoTypography.caption1Medium

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(27.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(top = 6.dp, bottom = 6.dp, start = 11.dp, end = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // tint 제거
        Icon(
            painter = painterResource(R.drawable.challenge_search),
            contentDescription = null,
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(11.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle,
                    color = DarkBrown20
                )
            }
            //caption1
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = textStyle.copy(color = BlackBrown),
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
            GallerySearchBar(value = query, onValueChange = { query = it })
        }
    }
}

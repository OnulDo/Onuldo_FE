package com.example.onuldo_fe.ui.screen.party.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.White

@Composable
fun PartyChallengeSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "생활루틴"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val borderColor = if (focused) Persimmon.copy(alpha = 0.4f) else DarkBrown40
    val iconColor = if (focused) Persimmon else DarkBrown
    val textStyle = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp, lineHeight = 13.sp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(27.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 11.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.party_challenge_search),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(11.dp))
        Box(Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(text = placeholder, style = textStyle, color = DarkBrown40)
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

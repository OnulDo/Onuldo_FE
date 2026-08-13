package com.example.onuldo_fe.ui.screen.challenge.participate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White


 // 다이얼로그용 버튼 (길어서 뺌)

@Composable
fun DialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = false
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (filled) Persimmon else White)
            .then(
                if (filled) Modifier
                else Modifier.border(1.5.dp, DarkBrown40, RoundedCornerShape(14.dp))
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = OnulDoTypography.body4Bold,
            color = if (filled) White else BlackBrown
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 350)
@Composable
private fun ChallengeDialogButtonPreview() {
    OnulDo_FETheme {
        Row(
            modifier = Modifier
                .background(SourCream)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DialogButton(
                text = "취소",
                onClick = {},
                modifier = Modifier.width(144.dp)
            )
            DialogButton(
                text = "포인트 충전",
                onClick = {},
                filled = true,
                modifier = Modifier.width(158.dp)
            )
        }
    }
}

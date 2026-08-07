package com.example.onuldo_fe.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
fun OnulDoErrorDialog(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    val spacing = LocalSpacing.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.width(280.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Persimmon20),
            color = SourCream
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = spacing.spacing28,
                            vertical = spacing.spacing28
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = BlackBrown,
                        textAlign = TextAlign.Center
                    )

                    androidx.compose.foundation.layout.Spacer(
                        modifier = Modifier.height(spacing.spacing12)
                    )

                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkBrown70,
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(color = DarkBrown20)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(SourCream)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(SourCream)
                            .clickable(onClick = onButtonClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Persimmon
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "오늘도 에러 다이얼로그",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun OnulDoErrorDialogPreview() {
    OnulDo_FETheme {
        OnulDoErrorDialog(
            title = "업로드에 실패했어요",
            description = "촬영 사진을 업로드하는데 실패했어요.\n인터넷 연결을 확인 후 재시도 해주세요.",
            buttonText = "확인",
            onButtonClick = {}
        )
    }
}

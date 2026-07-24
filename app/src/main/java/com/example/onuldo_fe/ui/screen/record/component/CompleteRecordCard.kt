package com.example.onuldo_fe.ui.screen.record.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.White

@Composable
fun CompleteRecordCard(
    isSuccess: Boolean,
    title: String,
    progress: Int,
    completeDate:String,
    point: Int,
) {
    val chipText = if(isSuccess) "성공" else "실패"
    val chipBackground = if(isSuccess) Green2 else Red2
    val cardTextColor = if(isSuccess) Green else Red

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Persimmon20),
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp),
        ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier
                        .width(52.dp)
                        .height(22.dp),
                    shape = RoundedCornerShape(11.dp),
                    color = chipBackground
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chipText,
                            style = MaterialTheme.typography.titleSmall,
                            color = cardTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BlackBrown
                )

                Text(
                    text = "${if (isSuccess) "+" else "-"}${point}P",
                    style = MaterialTheme.typography.bodyMedium,
                    color = cardTextColor
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "달성률 $progress%",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkBrown
                )
                Text(
                    text = completeDate + if (isSuccess) " 완료" else " 실패",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkBrown50
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompleteRecordCardTruePreview() {
    OnulDo_FETheme {
        CompleteRecordCard(
            isSuccess = true,
            title = "독서30분",
            progress = 92,
            completeDate = "2026.04.15",
            point = 18400,
        )
    }
}

@Preview(showBackground = false)
@Composable
fun CompleteRecordCardFalsePreview() {
    OnulDo_FETheme {
        CompleteRecordCard(
            isSuccess = false,
            title = "독서30분",
            progress = 92,
            completeDate = "2026.04.15",
            point = 18400,
        )
    }
}


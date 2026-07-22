package com.example.onuldo_fe.ui.screen.record.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.White

@Composable
fun ProgressRecordCard(
    category: String,
    title: String,
    dDay: Int,
    progress: Int,
    point: Int,
    isTodayVerified: Boolean
){
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Persimmon20),
        modifier = Modifier
            .height(170.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkBrown
                )

                Text(
                    text = "D-$dDay",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkBrown
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = BlackBrown
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isTodayVerified) "오늘 인증 완료"
                else "오늘 인증 필요",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isTodayVerified) Green else Persimmon
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 달성률 뱃지
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = if (isTodayVerified)
                    Green2
                else
                    Persimmon10
            ) {
                Text(
                    text = "달성률 $progress%",
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isTodayVerified)
                        Green
                    else
                        Persimmon
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (isTodayVerified)
                    Green
                else
                    Persimmon,
                trackColor = Persimmon10
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "예치 ${point}P",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkBrown50
            )
        }
    }
}
@Preview(showBackground = false)
@Composable
fun ProgressRecordCardFalsePreview() {
    OnulDo_FETheme {
        ProgressRecordCard(
            category = "생활루틴",
            title = "새벽 6시 기상",
            dDay = 15,
            progress = 85,
            point = 30000,
            isTodayVerified = false
        )
    }
}
@Preview(showBackground = true)
@Composable
fun ProgressRecordCardTruePreview() {
    OnulDo_FETheme {
        ProgressRecordCard(
            category = "생활루틴",
            title = "새벽 6시 기상",
            dDay = 15,
            progress = 85,
            point = 30000,
            isTodayVerified = true
        )
    }
}

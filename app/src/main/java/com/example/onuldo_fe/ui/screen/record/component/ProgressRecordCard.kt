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
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.White

@Composable
fun ProgressRecordCard(
    category: String,
    title: String,
    dDay: Int,
    progress: Int,
    depositAmount: Int,
    isTodayVerified: Boolean
) {
    val spacing = LocalSpacing.current
    val statusColor = if (isTodayVerified) Green else Persimmon
    Surface(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, DarkBrown40),
        color = White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(category, style = MaterialTheme.typography.titleSmall, color = DarkBrown)
                Text(if (dDay == 0) "D-Day" else "D-$dDay", style = MaterialTheme.typography.titleSmall, color = DarkBrown)
            }
            Spacer(Modifier.height(spacing.spacing8))
            Text(title, style = MaterialTheme.typography.bodyLarge, color = BlackBrown)
            Spacer(Modifier.height(spacing.spacing8))
            Text(
                if (isTodayVerified) "오늘 인증 완료" else "오늘 인증 필요",
                style = MaterialTheme.typography.titleSmall,
                color = statusColor
            )
            Spacer(Modifier.height(spacing.spacing12))
            Text("달성률 $progress%", style = MaterialTheme.typography.titleSmall, color = statusColor)
            Spacer(Modifier.height(spacing.spacing8))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0, 100) / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = statusColor,
                trackColor = Persimmon10
            )
            Spacer(Modifier.height(spacing.spacing12))
            Text(
                "예치금 ${"%,d".format(depositAmount)}P · 예상 환급 계산 예정",
                style = MaterialTheme.typography.labelSmall,
                color = DarkBrown50
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgressRecordCardPreview() {
    OnulDo_FETheme {
        ProgressRecordCard("개인 챌린지", "매일 6시 기상", 13, 72, 30000, false)
    }
}
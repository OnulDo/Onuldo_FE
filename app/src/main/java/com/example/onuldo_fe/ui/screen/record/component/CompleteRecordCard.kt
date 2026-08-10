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
import com.example.onuldo_fe.ui.theme.Black
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.White

@Composable
fun CompleteRecordCard(
    isSuccess: Boolean,
    title: String,
    progress: Int,
    completeDate: String,
    depositAmount: Int,
    point: Int
) {
    val statusColor = if (isSuccess) Green else Red
    Surface(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, DarkBrown40),
        color = White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(15.dp)) {
            Surface(
                modifier = Modifier.width(52.dp).height(22.dp),
                shape = RoundedCornerShape(11.dp),
                color = if (isSuccess) Green2 else Red2
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isSuccess) "성공" else "실패",
                        style = MaterialTheme.typography.titleSmall,
                        color = statusColor
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = BlackBrown)
                Text(
                    text = if (isSuccess) "보너스" else "차감",
                    style = MaterialTheme.typography.bodySmall,
                    color = Black
                )
            }
            Spacer(Modifier.height(3.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("달성률 $progress%", style = MaterialTheme.typography.labelMedium, color = DarkBrown)
                Text(
                    text = "${if (point > 0) "+" else ""}${"%,d".format(point)}P",
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor
                )
            }
            Spacer(Modifier.height(2.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    "$completeDate ${if (isSuccess) "완료" else "실패"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkBrown70
                )
                Text(
                    text = "예치금 ${"%,d".format(depositAmount)}P",
                    style = MaterialTheme.typography.labelMedium,
                    color = DarkBrown70
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompleteRecordCardPreview() {
    OnulDo_FETheme {
        CompleteRecordCard(true, "매일 6시 기상", 92, "2026-08-10", 30_000, 2_500)
    }
}

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Red

@Composable
private fun SummaryItem(
    title: String,
    value: String,
    valueColor: Color,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = title,
            style = OnulDoTypography.caption3Bold,
            color = DarkBrown70
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = value,
            style = OnulDoTypography.title2ExtraBold,
            color = valueColor
        )
    }
}

@Composable
fun RecordSummaryCard(
    completeCount: Int,
    successRate: Int,
    totalPoint: Int,
) {

    //총적립 텍스트 +/- 조건 처리
    val pointText = when {
        totalPoint > 0 -> "+${String.format("%,d", totalPoint)}P"
        totalPoint < 0 -> "${String.format("%,d", totalPoint)}P"
        else -> "0P"
    }

    //총 적립 텍스트 색상 조건 처리
    val pointColor = when {
        totalPoint > 0 -> Persimmon
        totalPoint < 0 -> Red
        else -> BlackBrown
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Persimmon10,
        border = BorderStroke(1.dp, Persimmon20),
        modifier = Modifier.height(80.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            SummaryItem(
                title = "총 완료",
                value = "${completeCount}개",
                valueColor = BlackBrown
            )


            SummaryItem(
                title = "성공률",
                value = "${successRate}%",
                valueColor = if (successRate >= 85) Green else Red
            )

            SummaryItem(
                title = "총 적립",
                value = pointText,
                valueColor = pointColor
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RecordSummaryCardPreview() {
    OnulDo_FETheme {
        RecordSummaryCard(
            completeCount = 12,
            successRate = 83,
            totalPoint = 298000
        )
    }
}
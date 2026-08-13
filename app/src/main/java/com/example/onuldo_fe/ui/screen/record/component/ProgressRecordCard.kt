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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.model.record.OngoingDailyStatus

@Composable
fun ProgressRecordCard(
    category: String,
    title: String,
    dDay: Int,
    progress: Int,
    depositAmount: Int,
    dailyStatus: OngoingDailyStatus
) {
    val spacing = LocalSpacing.current
    val statusStyle = dailyStatus.toRecordStatusStyle()
    Surface(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, DarkBrown40),
        color = White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(category, style = OnulDoTypography.caption3Bold, color = DarkBrown)
                Text(if (dDay == 0) "D-Day" else "D-$dDay", style =OnulDoTypography.caption3Bold, color = DarkBrown)
            }
            Spacer(Modifier.height(spacing.spacing8))
            Text(title, style = OnulDoTypography.body2Bold, color = BlackBrown)
            Spacer(Modifier.height(spacing.spacing8))
            Text(
                statusStyle.label,
                style = OnulDoTypography.caption3Bold,
                color = statusStyle.contentColor
            )
            Spacer(Modifier.height(spacing.spacing12))
            Column() {
                Surface(
                    modifier = Modifier.width(96.dp).height(22.dp),
                    shape = RoundedCornerShape(11.dp),
                    color = statusStyle.chipBackgroundColor
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "달성률 $progress%",
                            style = OnulDoTypography.caption3Bold,
                            color = statusStyle.contentColor
                        )
                    }
                }
            }
            Spacer(Modifier.height(spacing.spacing8))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0, 100) / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusStyle.contentColor,
                trackColor = statusStyle.trackColor,
                strokeCap = StrokeCap.Butt,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
            Spacer(Modifier.height(spacing.spacing10))
            Text(
                "예치 ${"%,d".format(depositAmount)}P",
                style = OnulDoTypography.caption3Regular,
                color = DarkBrown50
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgressRecordCardPreview() {
    OnulDo_FETheme {
        ProgressRecordCard(
            "개인 챌린지",
            "매일 6시 기상",
            13,
            72,
            30000,
            OngoingDailyStatus.NEED_CERTIFICATION
        )
    }
}

private data class RecordStatusStyle(
    val label: String,
    val contentColor: Color,
    val chipBackgroundColor: Color,
    val trackColor: Color
)

private fun OngoingDailyStatus.toRecordStatusStyle(): RecordStatusStyle = when (this) {
    OngoingDailyStatus.SUCCESS -> RecordStatusStyle(
        label = "오늘 인증 완료",
        contentColor = Green,
        chipBackgroundColor = Green2,
        trackColor = Green2
    )
    OngoingDailyStatus.FAILURE -> RecordStatusStyle(
        label = "오늘 인증 실패",
        contentColor = Red,
        chipBackgroundColor = Red2,
        trackColor = Red2
    )
    OngoingDailyStatus.REVIEW_PENDING -> RecordStatusStyle(
        label = "검토 대기 중",
        contentColor = DarkBrown,
        chipBackgroundColor = com.example.onuldo_fe.ui.theme.DarkBrown10,
        trackColor = com.example.onuldo_fe.ui.theme.DarkBrown10
    )
    OngoingDailyStatus.NEED_CERTIFICATION -> RecordStatusStyle(
        label = "오늘 인증 필요",
        contentColor = Persimmon,
        chipBackgroundColor = Persimmon10,
        trackColor = Persimmon10
    )
}

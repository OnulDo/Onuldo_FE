package com.example.onuldo_fe.ui.screen.home.notification.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.home.notification.NotificationItem
import com.example.onuldo_fe.ui.screen.home.notification.NotificationType
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

@Composable
fun NotificationCard(
    notification: NotificationItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(76.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(
                border = BorderStroke(1.dp, DarkBrown40),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        NotificationIcon(
            type = notification.type,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        )

        Column(
            modifier = Modifier.padding(start = 58.dp, top = 14.dp)
        ) {
            Text(
                text = notification.title,
                color = BlackBrown,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.description,
                color = DarkBrown70,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = notification.timeAgo,
                color = DarkBrown50,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun NotificationIcon(
    type: NotificationType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .background(type.iconBackgroundColor(), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = type.iconResId()),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            contentScale = ContentScale.Fit
        )
    }
}

private fun NotificationType.iconResId(): Int = when (this) {
    NotificationType.Deadline -> R.drawable.notification_deadline_icon
    NotificationType.Success,
    NotificationType.Settlement -> R.drawable.notification_success_icon
    NotificationType.NewChallenge -> R.drawable.notification_challenge_icon
    NotificationType.Failure -> R.drawable.notification_failure_icon
}

private fun NotificationType.iconBackgroundColor() = when (this) {
    NotificationType.Deadline,
    NotificationType.NewChallenge -> Persimmon10

    NotificationType.Success,
    NotificationType.Settlement -> Green2

    NotificationType.Failure -> Red2
}

@Preview(
    name = "Notification Card",
    showBackground = true,
    backgroundColor = 0xFFFFFDF7,
    widthDp = 390
)
@Composable
private fun NotificationCardPreview() {
    OnulDo_FETheme {
        NotificationCard(
            notification = NotificationItem(
                title = "인증 마감 30분 전이에요",
                description = "30분 러닝 챌린지 인증을 잊지 마세요",
                timeAgo = "방금",
                type = NotificationType.Deadline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(SourCream)
                .padding(horizontal = 20.dp)
        )
    }
}

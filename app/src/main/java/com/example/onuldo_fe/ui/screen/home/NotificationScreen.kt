package com.example.onuldo_fe.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.model.home.notification.NotificationItem
import com.example.onuldo_fe.model.home.notification.NotificationType
import com.example.onuldo_fe.repository.notification.NotificationRepositoryImpl
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.notification.NotificationUiState
import com.example.onuldo_fe.viewmodel.notification.toUiState

// 알림 종류별 아이콘 매핑 — API 연동 후에도 UI에서만 관리
private fun NotificationType.iconRes(): Int = when (this) {
    NotificationType.DeadlineReminder -> R.drawable.notification_deadline_icon
    NotificationType.DeadlineWarning -> R.drawable.notification_alert_icon
    NotificationType.ReviewPassed -> R.drawable.verification_check_icon
    NotificationType.ReviewRejected -> R.drawable.notification_fail_icon
    NotificationType.PartyMemberVerified -> R.drawable.notification_party_icon
    NotificationType.ChallengeStart -> R.drawable.notification_start_icon
    NotificationType.ChallengeEndReminder -> R.drawable.notification_deadline_icon
    NotificationType.SoloRefund -> R.drawable.verification_check_icon
    NotificationType.PartySettlement -> R.drawable.verification_check_icon
    NotificationType.PartyDailySettlement -> R.drawable.ic_email_badge
}

// 알림 화면 — 알림 목록
@Composable
fun NotificationScreen(
    uiState: NotificationUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    val notifications = uiState.notifications
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
        // .statusBarsPadding()        ← 제거 (Scaffold가 이미 처리)
    ) {
        // 상단 바 (뒤로가기 + 알림)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            OnulDoBackButton(
                onClick = onBackClick,
                // 패딩 없이 정렬만 — IconButton 중앙정렬로 화살표가 가로 20에 맞음(본문과 정렬)
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = "알림",
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = BlackBrown
            )
        }

        if (notifications.isEmpty()) {
            // 알림 없을 때 — 빈 화면
            NotificationEmpty()
        } else {
            // 알림 리스트
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = spacing.spacing20,
                    end = spacing.spacing20,
                    top = spacing.spacing8,
                    bottom = spacing.spacing20
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.spacing12)
            ) {
                items(notifications) { item ->
                    NotificationItemCard(item)
                }
            }
        }
    }
}

// 알림이 없을 때 빈 화면
@Composable
private fun ColumnScope.NotificationEmpty() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.notification_empty_icon),
            contentDescription = null,
            modifier = Modifier.size(width = 60.dp, height = 75.dp)
        )
        Spacer(Modifier.height(19.dp))
        Text(
            text = "새로운 알림이 없어요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            color = Persimmon20
        )
    }
}

// 알림 카드 — 내용(사유 등)에 따라 높이가 늘어남 (기본 76dp, 2줄이면 커짐)
@Composable
private fun NotificationItemCard(item: NotificationItem) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
            .padding(horizontal = spacing.spacing16, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘
        Image(
            painter = painterResource(item.type.iconRes()),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )

        Spacer(Modifier.width(spacing.spacing12))

        Column(modifier = Modifier.weight(1f)) {
            // 제목
            Text(
                text = item.title,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = BlackBrown,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            // 내용 — 받은 내용 그대로 출력 (\n 포함, 줄 수 제한 없음)
            Text(
                text = item.content,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = DarkBrown70
            )

            Spacer(Modifier.height(6.dp))

            // 시간 (상대 시각 — createdAt을 RelativeTime이 변환)
            Text(
                text = item.timeLabel,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                lineHeight = 10.sp,
                color = DarkBrown50
            )
        }
    }
}

@Preview(name = "Notification With Content", showBackground = true, widthDp = 390, heightDp = 1150)
@Composable
private fun NotificationScreenPreview() {
    OnulDo_FETheme {
        NotificationScreen(uiState = NotificationRepositoryImpl().getNotifications().toUiState())
    }
}

@Preview(name = "Notification Empty", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NotificationScreenEmptyPreview() {
    OnulDo_FETheme {
        NotificationScreen(uiState = NotificationUiState())
    }
}

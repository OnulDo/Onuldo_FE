package com.example.onuldo_fe.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.notification.NotificationUiState
import com.example.onuldo_fe.viewmodel.notification.toUiState

// 알림 종류별 아이콘 매핑 — API 연동 후에도 UI에서만 관리
private fun NotificationType.iconRes(): Int = when (this) {
    NotificationType.Deadline -> R.drawable.notification_deadline_icon
    NotificationType.VerificationSuccess -> R.drawable.verification_check_icon
    NotificationType.ChallengeStart -> R.drawable.notification_challenge_icon
    NotificationType.Refund -> R.drawable.verification_check_icon
    NotificationType.VerificationFail -> R.drawable.notification_fail_icon
}

// 알림 화면 — 알림 목록
@Composable
fun NotificationScreen(
    uiState: NotificationUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
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
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
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
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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

// 알림 카드
@Composable
private fun NotificationItemCard(item: NotificationItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
    ) {
        //아이콘
        Image(
            painter = painterResource(item.type.iconRes()),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(30.dp)
        )

        // 제목
        Text(
            text = item.title,
            modifier = Modifier.padding(start = 58.dp, end = 16.dp, top = 14.dp),
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            lineHeight = 13.sp,
            color = BlackBrown,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // 내용
        Text(
            text = item.content,
            modifier = Modifier.padding(start = 58.dp, end = 16.dp, top = 34.dp),
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 11.sp,
            color = DarkBrown70,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // 시간
        Text(
            text = item.time,
            modifier = Modifier.padding(start = 58.dp, top = 54.dp),
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            lineHeight = 10.sp,
            color = DarkBrown50
        )
    }
}

@Preview(name = "Notification With Content", showBackground = true, widthDp = 390, heightDp = 844)
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

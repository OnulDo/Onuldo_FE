package com.example.onuldo_fe.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.LaunchedEffect
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
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.notification.NotificationUiState

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    uiState: NotificationUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onItemClick: (NotificationItem) -> Unit = {},
    onRetry: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    val notifications = uiState.notifications
    val pullState = rememberPullToRefreshState()
    // 당겨서 새로고침 — 목록이 있을 때만 인디케이터 표시
    val isRefreshing = uiState.isLoading && notifications.isNotEmpty()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullState,
        modifier = modifier
            .fillMaxSize()
            .background(SourCream),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                color = Persimmon,
            )
        }
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
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

        val error = uiState.errorMessage
        when {
            // 첫 페이지 로딩 중 — 빈 상태·에러와 구분
            uiState.isLoading && notifications.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Persimmon20) }
            }
            // 첫 로딩 실패 — 에러 문구 + 다시 시도
            error != null && notifications.isEmpty() -> {
                NotificationError(message = error, onRetry = onRetry)
            }
            // 로딩 끝 + 진짜 비었을 때만 빈 화면
            uiState.isEmpty -> NotificationEmpty()
            else -> {
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
                        NotificationItemCard(item, onClick = { onItemClick(item) })
                    }
                    // 목록 끝 도달 → 다음 페이지(커서 페이징)
                    if (uiState.hasNext) {
                        item {
                            LaunchedEffect(notifications.size) { onLoadMore() }
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Persimmon20) }
                        }
                    }
                }
            }
        }
      }
    }
}

// 첫 로딩 실패 시 에러 + 다시 시도
@Composable
private fun ColumnScope.NotificationError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = BlackBrown,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "다시 시도",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Persimmon20,
            modifier = Modifier.clickable(onClick = onRetry)
        )
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
private fun NotificationItemCard(item: NotificationItem, onClick: () -> Unit = {}) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
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
                color = DarkBrown
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
        NotificationScreen(
            uiState = NotificationUiState(
                notifications = listOf(
                    NotificationItem(1, "지금 인증할 시간이에요", "새벽 6시 기상 인증이 시작됐어요.", "3분 전", "", NotificationType.DeadlineReminder),
                    NotificationItem(2, "인증이 승인됐어요", "30분 러닝 인증이 통과했어요.", "1시간 전", "", NotificationType.ReviewPassed),
                    NotificationItem(3, "동동님이 인증을 완료했어요", "파티 피드에서 확인해보세요", "어제", "", NotificationType.PartyMemberVerified),
                )
            )
        )
    }
}

@Preview(name = "Notification Empty", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NotificationScreenEmptyPreview() {
    OnulDo_FETheme {
        NotificationScreen(uiState = NotificationUiState())
    }
}

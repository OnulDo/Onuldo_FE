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

data class NotificationItem(
    val title: String,
    val content: String,
    val time: String,        // TODO: 실제로는 시간 계산? "방금/어제/N일 전" 표기
    val iconRes: Int
)

// 더미 데이터 — API 연동 시 교체
private val dummyNotifications = listOf( //TODO: 아이콘 다운 받아서 수정하기!
    NotificationItem("인증 마감 30분 전이에요", "30분 러닝 챌린지 인증을 잊지 마세요", "방금", R.drawable.verification_check_icon),
    NotificationItem("인증이 완료되었어요", "새벽 기상 챌린지 인증 성공 (+850P)", "5시간 전", R.drawable.verification_check_icon),
    NotificationItem("새 챌린지가 시작되었어요", "오늘부터 러닝 챌린지가 시작됐어요", "어제", R.drawable.verification_check_icon),
    NotificationItem("환급이 완료되었어요", "독서 30분 챌린지 환급 18,400P 지급", "2월 3일", R.drawable.verification_check_icon),
    NotificationItem("인증 실패로 차감되었어요", "5/17 새벽 기상 인증 미수행 (-850P)", "3일 전", R.drawable.verification_check_icon)
)

// 알림 화면 — 알림 목록
@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    notifications: List<NotificationItem> = dummyNotifications,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
    ) {
        // 상단 바 (뒤로가기 + 알림)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
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
            painter = painterResource(R.drawable.ic_notification_empty),
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
            painter = painterResource(item.iconRes),
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

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NotificationScreenPreview() {
    OnulDo_FETheme {
        NotificationScreen()
    }
}

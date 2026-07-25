package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.OnulDoSwitch
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

// 마이페이지 - 알림 설정
// 상태/유형 모델은 분리: [NotificationSettingsState], [NotificationType]

private val ContentWidth = 350.dp

@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(NotificationSettingsState()) }

    fun updateState(update: NotificationSettingsState.() -> NotificationSettingsState) {
        state = state.update()

        // TODO(API 연동 시)
        // repository.saveNotificationSetting(state)
    }

    // 전체 알림이 꺼지면 개별 알림은 값을 유지한 채 비활성 표시만 한다! (버튼 누르기 비활성)
    val subEnabled = state.all

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(26.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            OnulDoBackButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = onBackClick
            )

            Text(
                text = "알림 설정",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                color = BlackBrown
            )
        }

        Spacer(Modifier.height(39.dp))

        SettingToggleRow(
            title = "전체 알림 수신",
            description = "모든 알림을 한 번에 끄거나 켤 수 있어요",
            checked = state.all,
            onCheckedChange = { updateState { copy(all = it) } },
            height = 80.dp,
            backgroundColor = Persimmon10,
            borderColor = Persimmon20
        )

        Spacer(Modifier.height(29.dp))

        SettingSectionHeader(text = "챌린지 알림")

        Spacer(Modifier.height(9.dp))

        SettingToggleRow(
            title = "챌린지 시작 알림",
            description = "챌린지 시작 시각 알림",
            checked = state.challengeStart,
            onCheckedChange = { updateState { copy(challengeStart = it) } },
            enabled = subEnabled
        )

        Spacer(Modifier.height(8.dp))

        SettingToggleRow(
            title = "인증 마감 알림",
            description = "인증 마감 30분 전 알림",
            checked = state.deadline,
            onCheckedChange = { updateState { copy(deadline = it) } },
            enabled = subEnabled
        )

        Spacer(Modifier.height(8.dp))

        SettingToggleRow(
            title = "인증 결과 알림",
            description = "인증 성공/실패 결과 알림",
            checked = state.result,
            onCheckedChange = { updateState { copy(result = it) } },
            enabled = subEnabled
        )

        Spacer(Modifier.height(41.dp))

        SettingSectionHeader(text = "포인트 알림")

        Spacer(Modifier.height(9.dp))

        SettingToggleRow(
            title = "환급 완료 알림",
            description = "챌린지 종료 후 환급 알림",
            checked = state.refund,
            onCheckedChange = { updateState { copy(refund = it) } },
            enabled = subEnabled
        )

        Spacer(Modifier.height(8.dp))

        SettingToggleRow(
            title = "차감 알림",
            description = "인증 실패로 도전금 차감 시",
            checked = state.deduction,
            onCheckedChange = { updateState { copy(deduction = it) } },
            enabled = subEnabled
        )

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SettingSectionHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        color = DarkBrown50,
        modifier = modifier
            .width(ContentWidth)
            .padding(start = 4.dp)
    )
}

@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 64.dp,
    backgroundColor: Color = White,
    borderColor: Color = DarkBrown40
) {
    Row(
        modifier = modifier
            .width(ContentWidth)
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                color = BlackBrown
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = description,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                color = DarkBrown50
            )
        }

        Spacer(Modifier.width(12.dp))

        //버튼 전환
        OnulDoSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SettingScreenPreview() {
    OnulDo_FETheme {
        SettingScreen(onBackClick = {})
    }
}

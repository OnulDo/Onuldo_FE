package com.example.onuldo_fe.ui.screen.mypage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.OnulDoSwitch
import com.example.onuldo_fe.ui.component.PermissionDialogType
import com.example.onuldo_fe.ui.component.PermissionSettingDialog
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.mypage.NotificationSettingsViewModel

// 마이페이지 - 알림 설정
// 상태/유형 모델은 분리: [NotificationSettingsState], [NotificationType]

@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationSettingsViewModel = viewModel()
) {
    val context = LocalContext.current

    // 서버 값(GET /api/users/me/notification-settings)으로 채우고, 변경 시 PATCH로 저장한다.
    val state by viewModel.state.collectAsState()

    fun updateState(update: NotificationSettingsState.() -> NotificationSettingsState) {
        viewModel.apply(state.update())
    }

    // 진입 시 알림 권한이 없으면 안내 팝업을 띄운다(권한 없을 때 진입할 때마다).
    //TODO: API 33 미만은 런타임 알림 권한이 없어 항상 허용된 것으로 봄( 예외를 어떻게 할것?)
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val granted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        showNotificationPermissionDialog = !granted
    }

    // 전체 알림이 꺼지면 개별 알림도 모두 꺼짐 + 비활성
    val subEnabled = state.all

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
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
            onCheckedChange = { checked ->
                updateState {
                    copy(
                        all = checked,
                        challengeStart = checked,
                        deadline = checked,
                        endReminder = checked,
                        result = checked,
                        partyMemberVerified = checked,
                        refund = checked
                    )
                }
            },
            height = 80.dp,
            backgroundColor = Persimmon10,
            borderColor = Persimmon20
        )

        Spacer(Modifier.height(29.dp))

        SettingSectionHeader(text = "챌린지 알림")

        Spacer(Modifier.height(2.dp))

        SettingToggleRow(
            title = "인증 마감 리마인더",
            description = "당일 미인증 시 시간대별 리마인드 발송",
            checked = state.deadline,
            onCheckedChange = { updateState { copy(deadline = it) } },
            enabled = subEnabled
        )

        Spacer(Modifier.height(8.dp))

    //새 챌린지 시작
        SettingToggleRow(
            title = "새 챌린지 시작",
            description = "시작일 첫 인증 가능 시각에 알림",
            checked = state.challengeStart,
            onCheckedChange = { updateState { copy(challengeStart = it) } },
            enabled = subEnabled
        )

        Spacer(Modifier.height(8.dp))

    // 종료일 리마인더 (신규)
        SettingToggleRow(
            title = "종료일 리마인더",
            description = "종료 5·3·1일 전 20:00 알림",
            checked = state.endReminder,
            onCheckedChange = { updateState { copy(endReminder = it) } },
            enabled = subEnabled
        )

        Spacer(Modifier.height(41.dp))

        SettingSectionHeader(text = "인증 알림")

        Spacer(Modifier.height(2.dp))

    // 인증 결과
        SettingToggleRow(
            title = "인증 결과",
            description = "직접검토 통과·기각 결과 알림",
            checked = state.result,
            onCheckedChange = { updateState { copy(result = it) } },
            enabled = subEnabled
        )

        Spacer(Modifier.height(19.dp))

        SettingSectionHeader(text = "파티 알림")

        Spacer(Modifier.height(2.dp))

        // 파티원 인증 완료 — 인증 결과와 별개 상태
        SettingToggleRow(
            title = "파티원 인증 완료",
            description = "파티 피드에 인증 사진이 올라오면 알림",
            checked = state.partyMemberVerified,
            onCheckedChange = { updateState { copy(partyMemberVerified = it) } },
            enabled = subEnabled
        )

        Spacer(Modifier.height(41.dp))

        SettingSectionHeader(text = "항상 받는 알림")

        Spacer(Modifier.height(2.dp))

        SettingToggleRow(
            title = "정산·환급 완료",
            description = "포인트 지급 알림은 끌 수 없어요",
            checked = true,
            onCheckedChange = {},
            enabled = false,
            trailingText = "항상 발송"
        )
    }
    // 알림 권한 안내 팝업(권한x) → "설정으로 이동"이면 시스템 알림설정으로
    if (showNotificationPermissionDialog) {
        PermissionSettingDialog(
            type = PermissionDialogType.NOTIFICATION,
            // 취소 시: 팝업 닫고 전 화면
            onDismiss = {
                showNotificationPermissionDialog = false
                onBackClick()
            },
            onMoveToSettings = {
                showNotificationPermissionDialog = false
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
        )
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
        lineHeight = 22.sp,
        color = DarkBrown50,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp)
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
    showSwitch: Boolean = true,
    // 스위치 대신 오른쪽에 고정 문구를 띄울 때 사용 (예: "항상 발송")
    trailingText: String? = null,
    height: Dp = 64.dp,
    backgroundColor: Color = White,
    borderColor: Color = DarkBrown40
) {
    Row(
        modifier = modifier
            //사이즈가 작게 나와서 가로 padding기준으로 바꿈
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
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

        // 오른쪽: 고정 문구("항상 발송" 등)가 있으면 문구, 없으면 스위치
        when {
            trailingText != null -> Text(
                text = trailingText,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                color = DarkBrown50
            )

            showSwitch -> OnulDoSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SettingScreenPreview() {
    OnulDo_FETheme {
        SettingScreen(onBackClick = {})
    }
}

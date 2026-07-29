package com.example.onuldo_fe.ui.screen.party

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.screen.party.components.PartyInsufficientPointDialog
import com.example.onuldo_fe.ui.screen.party.components.PartyInviteCodeCard
import com.example.onuldo_fe.ui.screen.party.components.PartyTopBar
import com.example.onuldo_fe.ui.screen.party.components.PartyWaitingEmptySlotCard
import com.example.onuldo_fe.ui.screen.party.components.PartyWaitingMemberCard
import com.example.onuldo_fe.ui.theme.*
import com.example.onuldo_fe.viewmodel.party.PartyWaitingRoomUi

@Composable
fun PartyWaitingRoomScreen(
    ui: PartyWaitingRoomUi,
    isLeader: Boolean,
    availablePoint: Int = 5_000,
    isCurrentUserReady: Boolean = false,
    onBack: () -> Unit,
    onStartClick: () -> Unit = {},
    onReadyClick: () -> Unit = {},
    onChargePoint: () -> Unit = {},
    isActionInProgress: Boolean = false,
    errorMessage: String? = null
) {
    val spacing = LocalSpacing.current
    val clipboard = LocalClipboardManager.current
    var showPointDialog by remember { mutableStateOf(false) }

    // 상단 뒤로가기와 시스템 뒤로가기 모두 동일한 파티 탈퇴 로직 실행
    BackHandler(onBack = onBack)

    Column(Modifier.fillMaxSize().background(SourCream)) {
        PartyTopBar(title = "파티 대기방", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                // TODO 디자인 시스템에 40dp 토큰이 추가되면 LocalSpacing으로 교체
                .padding(
                    start = spacing.spacing20,
                    top = 40.dp,
                    end = spacing.spacing20,
                    bottom = spacing.spacing24
                )
        ) {
            PartyWaitingRoomInfoCard(ui)
            Spacer(Modifier.height(spacing.spacing16))
            PartyInviteCodeCard(
                ui.inviteCode,
                onCopyClick = { clipboard.setText(AnnotatedString(ui.inviteCode)) }
            )
            Spacer(Modifier.height(spacing.spacing24))
            Text(
                "파티원",
                // TODO 디자인 시스템에 4dp 토큰이 추가되면 LocalSpacing으로 교체
                Modifier.padding(start = 4.dp),
                color = BlackBrown,
                fontFamily = Pretendard,
                fontSize = 12.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(spacing.spacing12))

            ui.members.forEach { member ->
                PartyWaitingMemberCard(member)
                Spacer(Modifier.height(spacing.spacing8))
            }

            repeat((ui.capacity - ui.members.size).coerceAtLeast(0)) {
                PartyWaitingEmptySlotCard()
                Spacer(Modifier.height(spacing.spacing8))
            }

        }

        Box(Modifier.fillMaxWidth().height(138.dp).background(SourCream), contentAlignment = Alignment.TopCenter) {
            Text(
                text = errorMessage ?: "전원이 모이면 파티장이 시작할 수 있어요",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spacing20)
                    // TODO 디자인 시스템에 6dp 토큰이 추가되면 LocalSpacing으로 교체
                    .offset(y = 6.dp),
                color = if (errorMessage == null) DarkBrown50 else Persimmon,
                fontFamily = Pretendard,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = {
                    when {
                        isLeader -> onStartClick()
                        availablePoint < ui.deposit -> showPointDialog = true
                        else -> onReadyClick()
                    }
                },
                enabled = !isActionInProgress && if (isLeader) ui.canStart else !isCurrentUserReady,
                // TODO 디자인 시스템에 40dp 토큰이 추가되면 LocalSpacing으로 교체
                modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.spacing20).padding(top = 40.dp).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Persimmon, contentColor = SourCream, disabledContainerColor = DarkBrown10, disabledContentColor = DarkBrown40)
            ) {
                Text(
                    if (isActionInProgress) "처리 중..." else if (isLeader) "시작하기" else "준비완료",
                    fontFamily = Pretendard,
                    fontSize = 17.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showPointDialog) {
        PartyInsufficientPointDialog(
            ownedPoint = availablePoint,
            requiredPoint = ui.deposit,
            onDismiss = { showPointDialog = false },
            onChargeClick = {
                showPointDialog = false
                onChargePoint()
            }
        )
    }
}

@Composable
private fun PartyWaitingRoomInfoCard(ui: PartyWaitingRoomUi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(Persimmon10, RoundedCornerShape(14.dp))
            .border(1.dp, Persimmon20, RoundedCornerShape(14.dp))
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            ui.partyName,
            color = BlackBrown,
            fontFamily = Pretendard,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "모집중 · ${ui.members.size}/${ui.capacity}명 · ${ui.period} · 1인 ${"%,d".format(ui.deposit)}P",
            // TODO 디자인 시스템에 3dp 토큰이 추가되면 LocalSpacing으로 교체
            Modifier.padding(top = 3.dp),
            color = DarkBrown,
            fontFamily = Pretendard,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(name = "대기방 파티 정보", showBackground = true, widthDp = 390)
@Composable
private fun PartyWaitingRoomInfoCardPreview() {
    OnulDo_FETheme {
        PartyWaitingRoomInfoCard(ui = PartyWaitingRoomUi(), modifier = Modifier.padding(20.dp))
    }
}

@Preview(name = "대기방 - 파티장", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyWaitingRoomLeaderPreview() {
    OnulDo_FETheme { PartyWaitingRoomScreen(ui = PartyWaitingRoomUi(), isLeader = true, onBack = {}) }
}

@Preview(name = "대기방 - 파티원", showBackground = true, widthDp = 390, heightDp = 844)
@Composable private fun PartyWaitingRoomMemberPreview() {
    OnulDo_FETheme { PartyWaitingRoomScreen(ui = PartyWaitingRoomUi(), isLeader = false, onBack = {}) }
}
